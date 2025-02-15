package kr.kro.dokbaro.server.core.bookquiz.domain.exception

import kr.kro.dokbaro.server.common.exception.http.status4xx.NotFoundException

class NotFoundQuestionException(
	questionId: Long,
) : NotFoundException("question '$questionId' not found")