package kr.kro.dokbaro.server.core.book.application.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kr.kro.dokbaro.server.core.book.application.port.input.dto.CreateBookCategoryCommand
import kr.kro.dokbaro.server.core.book.application.port.out.InsertBookCategoryPort
import kr.kro.dokbaro.server.core.book.application.service.auth.BookAuthorityCheckService
import kr.kro.dokbaro.server.fixture.domain.dokbaroAdminFixture

class BookCategoryServiceTest :
	StringSpec({
		val insertBookCategoryPort = mockk<InsertBookCategoryPort>()
		val service = BookCategoryService(insertBookCategoryPort, BookAuthorityCheckService())

		"카테고리 생성을 수행한다" {
			every { insertBookCategoryPort.insert(any()) } returns 1

			service.create(
				CreateBookCategoryCommand(
					"모바일",
					"MOBILE",
					3,
				),
				dokbaroAdminFixture(),
			) shouldNotBe null
		}
	})