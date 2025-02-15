package kr.kro.dokbaro.server.core.bookquiz.adapter.input.web.dto

import kr.kro.dokbaro.server.core.bookquiz.application.port.input.dto.CreateBookQuizCommand
import kr.kro.dokbaro.server.core.bookquiz.domain.AccessScope

data class CreateBookQuizRequest(
	val title: String,
	val description: String,
	val bookId: Long,
	val questions: Collection<CreateBookQuizCommand.Question>,
	val studyGroupId: Long?,
	val timeLimitSecond: Int?,
	val viewScope: AccessScope,
	val editScope: AccessScope,
)