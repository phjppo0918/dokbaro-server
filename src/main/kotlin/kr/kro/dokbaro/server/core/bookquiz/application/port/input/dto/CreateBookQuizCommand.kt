package kr.kro.dokbaro.server.core.bookquiz.application.port.input.dto

import kr.kro.dokbaro.server.core.bookquiz.domain.AccessScope
import kr.kro.dokbaro.server.core.bookquiz.domain.QuizType
import java.util.UUID

data class CreateBookQuizCommand(
	val title: String,
	val description: String,
	val bookId: Long,
	val creatorAuthId: UUID,
	val questions: Collection<CreateQuizQuestionCommand>,
	val studyGroupId: Long? = null,
	val timeLimitSecond: Int? = null,
	val viewScope: AccessScope,
	val editScope: AccessScope,
)

data class CreateQuizQuestionCommand(
	val content: String,
	val selectOptions: Collection<String> = emptyList(),
	val answerExplanationContent: String,
	val answerExplanationImages: Collection<String> = emptyList(),
	val answerType: QuizType,
	val answers: Collection<String> = emptyList(),
)