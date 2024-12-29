package kr.kro.dokbaro.server.core.member.application.service

import kr.kro.dokbaro.server.core.account.application.port.input.UpdateAccountEmailUseCase
import kr.kro.dokbaro.server.core.emailauthentication.application.port.input.UseAuthenticatedEmailUseCase
import kr.kro.dokbaro.server.core.member.application.port.input.command.ModifyMemberUseCase
import kr.kro.dokbaro.server.core.member.application.port.input.command.RegisterMemberUseCase
import kr.kro.dokbaro.server.core.member.application.port.input.command.WithdrawMemberUseCase
import kr.kro.dokbaro.server.core.member.application.port.input.command.dto.ModifyMemberCommand
import kr.kro.dokbaro.server.core.member.application.port.input.command.dto.RegisterMemberCommand
import kr.kro.dokbaro.server.core.member.application.port.out.ExistMemberByEmailPort
import kr.kro.dokbaro.server.core.member.application.port.out.InsertMemberPort
import kr.kro.dokbaro.server.core.member.application.port.out.LoadMemberByCertificationIdPort
import kr.kro.dokbaro.server.core.member.application.port.out.UpdateMemberPort
import kr.kro.dokbaro.server.core.member.application.service.exception.AlreadyRegisteredEmailException
import kr.kro.dokbaro.server.core.member.application.service.exception.NotFoundMemberException
import kr.kro.dokbaro.server.core.member.domain.Email
import kr.kro.dokbaro.server.core.member.domain.Member
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberService(
	private val insertMemberPort: InsertMemberPort,
	private val updateMemberPort: UpdateMemberPort,
	private val existMemberByEmailPort: ExistMemberByEmailPort,
	private val loadMemberByCertificationIdPort: LoadMemberByCertificationIdPort,
	private val useAuthenticatedEmailUseCase: UseAuthenticatedEmailUseCase,
	private val updateAccountEmailUseCase: UpdateAccountEmailUseCase,
) : RegisterMemberUseCase,
	ModifyMemberUseCase,
	WithdrawMemberUseCase {
	override fun register(command: RegisterMemberCommand): Member {
		val certificationId = UUID.randomUUID()

		if (existMemberByEmailPort.existByEmail(command.email)) {
			throw AlreadyRegisteredEmailException(command.email)
		}

		return insertMemberPort.insert(
			Member(
				nickname = command.nickname,
				email = Email(command.email),
				profileImage = command.profileImage,
				certificationId = certificationId,
				accountType = command.accountType,
			),
		)
	}

	override fun modify(command: ModifyMemberCommand) {
		val member: Member =
			loadMemberByCertificationIdPort.findMemberByCertificationId(command.certificationId)
				?: throw NotFoundMemberException()

		command.email?.let {
			useAuthenticatedEmailUseCase.useEmail(email = command.email)
			updateAccountEmailUseCase.updateEmail(member.id, command.email)
		}

		member.modify(
			nickName = command.nickname,
			email = command.email?.let { Email(it) },
			profileImage = command.profileImage,
		)

		updateMemberPort.update(member)
	}

	override fun withdrawBy(authId: UUID) {
		val member: Member =
			loadMemberByCertificationIdPort.findMemberByCertificationId(authId)
				?: throw NotFoundMemberException()

		member.withdraw()

		updateMemberPort.update(member)
	}
}