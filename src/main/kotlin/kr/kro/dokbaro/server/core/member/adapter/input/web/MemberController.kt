package kr.kro.dokbaro.server.core.member.adapter.input.web

import kr.kro.dokbaro.server.common.util.UUIDUtils
import kr.kro.dokbaro.server.core.member.adapter.input.web.dto.ModifyMemberRequest
import kr.kro.dokbaro.server.core.member.application.port.input.command.ModifyMemberUseCase
import kr.kro.dokbaro.server.core.member.application.port.input.dto.ModifyMemberCommand
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
	private val modifyMemberUseCase: ModifyMemberUseCase,
) {
	@PutMapping("/login-user")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun modifyMember(
		auth: Authentication,
		@RequestBody request: ModifyMemberRequest,
	) {
		modifyMemberUseCase.modify(
			ModifyMemberCommand(
				certificationId = UUIDUtils.stringToUUID(auth.name),
				nickName = request.nickName,
				email = request.email,
				profileImage = request.profileImage,
			),
		)
	}
}