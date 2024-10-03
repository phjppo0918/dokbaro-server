package kr.kro.dokbaro.server.fixture.domain

import kr.kro.dokbaro.server.common.constant.Constants
import kr.kro.dokbaro.server.core.bookquiz.domain.AnswerFactory
import kr.kro.dokbaro.server.core.bookquiz.domain.AnswerSheet
import kr.kro.dokbaro.server.core.bookquiz.domain.Answerable
import kr.kro.dokbaro.server.core.bookquiz.domain.BookQuiz
import kr.kro.dokbaro.server.core.bookquiz.domain.QuizQuestion
import kr.kro.dokbaro.server.core.bookquiz.domain.QuizType
import kr.kro.dokbaro.server.core.bookquiz.domain.SelectOption

fun bookQuizFixture(
	title: String = "title",
	description: String = "description",
	bookId: Long = 0,
	creatorId: Long = 0,
	questions: Collection<QuizQuestion> = listOf(quizQuestionFixture()),
	id: Long = 0,
) = BookQuiz(
	title = title,
	description = description,
	bookId = bookId,
	creatorId = creatorId,
	questions = questions,
	id = id,
)

fun quizQuestionFixture(
	content: String = "content",
	selectOptions: Collection<SelectOption> = listOf(SelectOption("selectOption")),
	answerExplanation: String = "answer_explanation",
	answer: Answerable =
		AnswerFactory.create(
			QuizType.MULTIPLE_CHOICE,
			AnswerSheet(listOf("2", "4")),
		),
	id: Long = Constants.UNSAVED_ID,
) = QuizQuestion(
	content = content,
	selectOptions = selectOptions,
	answerExplanation = answerExplanation,
	answer = answer,
	id = id,
)