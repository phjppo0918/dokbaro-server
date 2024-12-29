package kr.kro.dokbaro.server.core.account.application.port.out

import kr.kro.dokbaro.server.core.account.domain.EmailAccount

fun interface InsertAccountPasswordPort {
	fun insertEmailAccount(emailAccount: EmailAccount)
}