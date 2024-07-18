package kr.kro.dokbaro.server.domain.auth.adapter.output.github

import com.navercorp.fixturemonkey.kotlin.setExp
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.kro.dokbaro.server.domain.auth.adapter.output.github.external.GithubAuthorizationClient
import kr.kro.dokbaro.server.domain.auth.adapter.output.github.external.GithubAuthorizationTokenResponse
import kr.kro.dokbaro.server.fixture.FixtureBuilder

class GithubResourceTokenLoaderTest :
	StringSpec({
		val githubAuthorizationClient = mockk<GithubAuthorizationClient>()
		val githubResourceTokenLoader = GithubResourceTokenLoader(githubAuthorizationClient, "token", "aa", "bb")

		"github의 accessToken을 발급받는다" {
			val sample =
				FixtureBuilder
					.give<GithubAuthorizationTokenResponse>()
					.setExp(
						GithubAuthorizationTokenResponse::accessToken,
						"asdfasdf",
					).sample()
			every { githubAuthorizationClient.getAuthorizationToken(any(), any(), any(), any()) } returns sample

			val result: String = githubResourceTokenLoader.getToken("token")

			result.isNotEmpty() shouldBe true
		}
	})