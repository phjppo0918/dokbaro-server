package kr.kro.dokbaro.server.domain.token.model.access.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Date

@Component
class AccessTokenGenerator(
	private val key: Key,
	private val clock: Clock,
	@Value("\${jwt.limit-millisecond}") private val expireMillisecond: Long,
) {
	fun generate(token: TokenClaims): String {
		val claims = mapToClaims(token)
		return compact(claims)
	}

	private fun mapToClaims(claim: TokenClaims): Claims =
		Jwts
			.claims()
			.add("id", claim.id)
			.add("role", claim.role)
			.build()

	private fun compact(claims: Claims): String {
		val now = ZonedDateTime.now(clock)

		return Jwts
			.builder()
			.claims(claims)
			.issuedAt(Date.from(now.toInstant()))
			.signWith(key)
			.expiration(Date.from(now.plusSeconds(expireMillisecond).toInstant()))
			.compact()
	}
}