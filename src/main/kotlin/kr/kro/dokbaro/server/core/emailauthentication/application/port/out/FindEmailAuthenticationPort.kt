package kr.kro.dokbaro.server.core.emailauthentication.application.port.out

import kr.kro.dokbaro.server.core.emailauthentication.application.port.out.dto.SearchEmailAuthenticationCondition
import kr.kro.dokbaro.server.core.emailauthentication.domain.EmailAuthentication

fun interface FindEmailAuthenticationPort {
	fun findBy(condition: SearchEmailAuthenticationCondition): EmailAuthentication?
}