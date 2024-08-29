package kr.kro.dokbaro.server.core.member.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class MemberTest :
	StringSpec({

		"이메일 체크를 수행한다" {
			val member =
				Member(
					nickName = "asdf",
					email = Email("asdf@example.com"),
					profileImage = "kk.png",
					certificationId = UUID.randomUUID(),
				)
			member.checkEmail()

			member.email.verified shouldBe true
		}

		"수정을 수행한다" {
			val member =
				Member(
					nickName = "asdf",
					email = Email("asdf@example.com"),
					profileImage = "kk.png",
					certificationId = UUID.randomUUID(),
				)

			val newNickName = "newNickname"
			val newEmail = Email("newEmail")
			val newImage = "new.png"
			member.modify(
				nickName = newNickName,
				email = newEmail,
				profileImage = newImage,
			)

			member.nickName shouldBe newNickName
			member.email shouldBe newEmail
			member.profileImage shouldBe newImage
		}

		"수정 시 null 값을 전송하면 기존 값을 유지한다" {
			val beforeNickName = "name"
			val beforeEmail = Email("mail")
			val beforeImage = "before.png"

			val member =
				Member(
					nickName = beforeNickName,
					email = beforeEmail,
					profileImage = beforeImage,
					certificationId = UUID.randomUUID(),
				)

			member.modify(
				nickName = null,
				email = null,
				profileImage = null,
			)

			member.nickName shouldBe beforeNickName
			member.email shouldBe beforeEmail
			member.profileImage shouldBe beforeImage
		}
	})