package kr.kro.dokbaro.server.core.quizreviewreport.domain

import kr.kro.dokbaro.server.common.constant.Constants

data class QuizReviewReport(
	val id: Long = Constants.UNSAVED_ID,
	val quizReviewId: Long,
	val reporterId: Long,
	val content: String,
)