package kr.kro.dokbaro.server.core.notification.application.port.input

import kr.kro.dokbaro.server.core.notification.query.NotificationResult

fun interface FindAllNotificationUseCase {
	fun findAllBy(loginUserId: Long): Collection<NotificationResult>
}