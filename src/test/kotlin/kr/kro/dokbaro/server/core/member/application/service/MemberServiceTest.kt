package kr.kro.dokbaro.server.core.member.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.kro.dokbaro.server.core.member.application.port.input.dto.ModifyMemberCommand
import kr.kro.dokbaro.server.core.member.application.port.input.dto.RegisterMemberCommand
import kr.kro.dokbaro.server.core.member.application.port.out.ExistMemberByEmailPort
import kr.kro.dokbaro.server.core.member.application.port.out.LoadMemberByCertificationIdPort
import kr.kro.dokbaro.server.core.member.application.port.out.SaveMemberPort
import kr.kro.dokbaro.server.core.member.domain.Email
import kr.kro.dokbaro.server.core.member.domain.Member
import kr.kro.dokbaro.server.fixture.FixtureBuilder
import java.util.UUID
import kotlin.random.Random

class MemberServiceTest :
	StringSpec({
		val saveMemberPort = mockk<SaveMemberPort>()
		val updateMemberPort = UpdateMemberPortMock()
		val existMemberEmailPort = mockk<ExistMemberByEmailPort>()
		val loadMemberByCertificationIdPort = mockk<LoadMemberByCertificationIdPort>()

		val memberService =
			MemberService(saveMemberPort, updateMemberPort, existMemberEmailPort, loadMemberByCertificationIdPort)

		afterEach {
			updateMemberPort.clear()
		}

		"저장을 수행한다" {
			val command =
				RegisterMemberCommand(
					nickName = "asdf",
					email = "kkk@gmail.com",
					profileImage = "profile.png",
				)

			val member =
				Member(
					nickName = command.nickName,
					email = Email(command.email),
					profileImage = command.profileImage,
					certificationId = UUID.randomUUID(),
					id = Random.nextLong(),
				)

			every { saveMemberPort.save(any()) } returns member
			every { existMemberEmailPort.existByEmail(command.email) } returns false

			memberService.register(command) shouldBe member
		}

		"중복된 이메일로 저장 시 예외를 반환한다" {
			val command =
				RegisterMemberCommand(
					nickName = "asdf",
					email = "kkk@gmail.com",
					profileImage = "profile.png",
				)
			every { existMemberEmailPort.existByEmail(command.email) } returns true

			shouldThrow<AlreadyRegisteredEmailException> {
				memberService.register(command)
			}
		}

		"메일 검증을 수행한다" {
			val targetUUID = UUID.randomUUID()

			every { loadMemberByCertificationIdPort.findByCertificationId(targetUUID) } returns
				Member(
					nickName = "nickname",
					email = Email("dasf"),
					profileImage = "image.png",
					certificationId = targetUUID,
					id = Random.nextLong(),
				)

			memberService.checkMail(targetUUID)

			updateMemberPort.storage!!.email.verified shouldBe true
		}

		"메일 검증 시 certificationId를 통한 member가 없으면 예외를 반환한다" {
			every { loadMemberByCertificationIdPort.findByCertificationId(any()) } returns null

			shouldThrow<NotFoundMemberException> {
				memberService.checkMail(UUID.randomUUID())
			}
		}

		"수정을 수행한다" {
			val targetUUID = UUID.randomUUID()
			val resentEmail = Email("dasf@kkk.com")
			every { loadMemberByCertificationIdPort.findByCertificationId(targetUUID) } returns
				Member(
					nickName = "nickname",
					email = resentEmail,
					profileImage = "image.png",
					certificationId = targetUUID,
					id = Random.nextLong(),
				)

			val command =
				ModifyMemberCommand(
					targetUUID,
					"newNick",
					"new@new.com",
					"hello.png",
				)

			memberService.modify(command)

			val result = updateMemberPort.storage!!

			result.nickName shouldBe command.nickName
			result.email shouldBe Email(command.email!!)
			result.profileImage shouldBe command.profileImage
		}

		"이메일을 제외하고 수정을 수행한다" {
			val targetUUID = UUID.randomUUID()
			val resentEmail = Email("dasf@kkk.com")
			every { loadMemberByCertificationIdPort.findByCertificationId(targetUUID) } returns
				Member(
					nickName = "nickname",
					email = resentEmail,
					profileImage = "image.png",
					certificationId = targetUUID,
					id = Random.nextLong(),
				)

			val command =
				ModifyMemberCommand(
					targetUUID,
					"newNick",
					null,
					"hello.png",
				)

			memberService.modify(command)

			val result = updateMemberPort.storage!!

			result.nickName shouldBe command.nickName
			result.email shouldBe resentEmail
			result.profileImage shouldBe command.profileImage
		}

		"수정 시 certificationId를 통한 member가 없으면 예외를 반환한다" {
			every { loadMemberByCertificationIdPort.findByCertificationId(any()) } returns null

			shouldThrow<NotFoundMemberException> {
				memberService.modify(FixtureBuilder.give<ModifyMemberCommand>().sample())
			}
		}
	})