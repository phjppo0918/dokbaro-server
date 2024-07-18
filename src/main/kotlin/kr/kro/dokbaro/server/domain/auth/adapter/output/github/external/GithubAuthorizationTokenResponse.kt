package kr.kro.dokbaro.server.domain.auth.adapter.output.github.external

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GithubAuthorizationTokenResponse(
	val accessToken: String,
	val scope: String,
	val tokenType: String,
)