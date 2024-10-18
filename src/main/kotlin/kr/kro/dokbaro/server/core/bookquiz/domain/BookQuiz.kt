package kr.kro.dokbaro.server.core.bookquiz.domain

import kr.kro.dokbaro.server.common.constant.Constants

data class BookQuiz(
	var title: String,
	var description: String,
	var bookId: Long,
	val creatorId: Long,
	val questions: QuizQuestions,
	var studyGroupId: Long? = null,
	var timeLimitSecond: Int? = null,
	var viewScope: AccessScope = AccessScope.EVERYONE,
	var editScope: AccessScope = AccessScope.CREATOR,
	val id: Long = Constants.UNSAVED_ID,
) {
	fun updateBasicOption(
		title: String = this.title,
		description: String = this.description,
		bookId: Long = this.bookId,
		studyGroupId: Long? = this.studyGroupId,
		timeLimitSecond: Int? = this.timeLimitSecond,
		viewScope: AccessScope = this.viewScope,
		editScope: AccessScope = this.editScope,
	) {
		this.title = title
		this.description = description
		this.bookId = bookId
		this.studyGroupId = studyGroupId
		this.timeLimitSecond = timeLimitSecond
		this.viewScope = viewScope
		this.editScope = editScope
	}

	fun updateQuestions(newQuestions: Collection<QuizQuestion>) {
		questions.updateQuestions(newQuestions)
	}
}