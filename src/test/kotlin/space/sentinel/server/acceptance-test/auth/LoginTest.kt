package space.sentinel.server.`acceptance-test`.auth

import io.netty.handler.codec.http.cookie.Cookie
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.api.request.LoginRequest
import space.sentinel.controller.AuthenticationController
import space.sentinel.controller.AuthenticationController.Companion.SESSION_ID
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.ValidLoginRequest
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class LoginTest : AcceptanceTest() {

    @Test
    fun `valid login data should return FOUND`() {
        val request = PostRequestBuilder(baseUri)
                .uri(AuthenticationController.CONTROLLER_LOGIN)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(ValidLoginRequest))

        StepVerifier
                .create(response)
                .expectNext(302)
                .verifyComplete()
    }

    @Test
    fun `valid login data should return session cookie`() {
        val loginRequest = LoginRequest(
                email = "test@body.ru",
                pass = LoginRequest.encodePassword("canihavehamburger?")
        )

        val request = PostRequestBuilder(baseUri)
                .uri(AuthenticationController.CONTROLLER_LOGIN)

        val response = PostRequester(request)
                .cookies(mapper.writeValueAsString(loginRequest))

        StepVerifier
                .create(response)
                .expectNextMatches { cookies ->
                    val get = cookies.getOrDefault("session_id", emptySet<Cookie>())

                    MatcherAssert.assertThat(get.isNotEmpty(), equalTo(true))

                    val value = get.iterator().next()

                    MatcherAssert.assertThat(value.isSecure, equalTo(true))
                    MatcherAssert.assertThat(value.name(), equalTo(SESSION_ID))
                    MatcherAssert.assertThat(value.value().length, equalTo(64))
                    MatcherAssert.assertThat(value.maxAge(), equalTo(86400L))

                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `invalid login data should return UNAUTHORIZED`() {
        val loginRequest = LoginRequest(
                email = "frankensteen@lo.fa",
                pass = LoginRequest.encodePassword("canihavehamburger?")
        )

        val request = PostRequestBuilder(baseUri)
                .uri(AuthenticationController.CONTROLLER_LOGIN)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(loginRequest))

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

    @Test
    fun `inactive user should receive UNAUTHORIZED`() {
        val loginRequest = LoginRequest(
                email = "john@carm.ack",
                pass = LoginRequest.encodePassword("")
        )

        val request = PostRequestBuilder(baseUri)
                .uri(AuthenticationController.CONTROLLER_LOGIN)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(loginRequest))

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

    @Test
    fun `logout works after login`() {
        val loginRequest = PostRequestBuilder(baseUri)
                .uri(AuthenticationController.CONTROLLER_LOGIN)

        val cookie = PostRequester(loginRequest)
                .cookies(mapper.writeValueAsString(ValidLoginRequest)).block()

        val sessionId = cookie!!.getValue(SESSION_ID).stream().filter { it.name() == "session_id" }.map { it.value() }.findAny().get()

        val logoutRequest = PostRequestBuilder(baseUri)
                .withAuth(sessionId)
                .uri(AuthenticationController.CONTROLLER_LOGOUT)

        val response = PostRequester(logoutRequest).statusCode()

        StepVerifier
                .create(response)
                .expectNext(200)
                .verifyComplete()
    }

    @Test
    fun `cannot logout with invalid session`() {
        val logoutRequest = PostRequestBuilder(baseUri)
                .withAuth("invalid")
                .uri(AuthenticationController.CONTROLLER_LOGOUT)

        val response = PostRequester(logoutRequest).statusCode()

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }
}