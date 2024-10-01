package kr.kro.dokbaro.server.core.studygroup.application.service

import kr.kro.dokbaro.server.core.member.application.port.input.dto.MemberResponse
import kr.kro.dokbaro.server.core.member.application.port.input.query.FindCertificatedMemberUseCase
import kr.kro.dokbaro.server.core.studygroup.application.port.input.CreateStudyGroupUseCase
import kr.kro.dokbaro.server.core.studygroup.application.port.input.JoinStudyGroupUseCase
import kr.kro.dokbaro.server.core.studygroup.application.port.input.dto.CreateStudyGroupCommand
import kr.kro.dokbaro.server.core.studygroup.application.port.input.dto.JoinStudyGroupCommand
import kr.kro.dokbaro.server.core.studygroup.application.port.out.InsertStudyGroupPort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.LoadStudyGroupByInviteCodePort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.UpdateStudyGroupPort
import kr.kro.dokbaro.server.core.studygroup.application.service.exception.NotFoundStudyGroupException
import kr.kro.dokbaro.server.core.studygroup.domain.StudyGroup
import org.springframework.stereotype.Service

@Service
class StudyGroupService(
	private val insertStudyGroupPort: InsertStudyGroupPort,
	private val findCertificatedMemberUseCase: FindCertificatedMemberUseCase,
	private val inviteCodeGenerator: InviteCodeGenerator,
	private val loadStudyGroupByInviteCodePort: LoadStudyGroupByInviteCodePort,
	private val updateStudyGroupPort: UpdateStudyGroupPort,
) : CreateStudyGroupUseCase,
	JoinStudyGroupUseCase {
	override fun create(command: CreateStudyGroupCommand): Long {
		val creator: MemberResponse = findCertificatedMemberUseCase.getByCertificationId(command.creatorAuthId)
		
		return insertStudyGroupPort.insert(
			StudyGroup(
				command.name,
				command.introduction,
				command.profileImageUrl,
				creator.id,
				inviteCodeGenerator.generate(),
			),
		)
	}

	override fun join(command: JoinStudyGroupCommand) {
		val studyGroup: StudyGroup =
			loadStudyGroupByInviteCodePort.findByInviteCode(
				command.inviteCode,
			) ?: throw NotFoundStudyGroupException(command.inviteCode)
		val memberId: MemberResponse = findCertificatedMemberUseCase.getByCertificationId(command.participantAuthId)

		studyGroup.join(memberId.id)

		updateStudyGroupPort.update(studyGroup)
	}
}