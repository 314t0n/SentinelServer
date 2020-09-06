package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonpath.matchers.JsonPathMatchers
import io.netty.handler.codec.http.cookie.DefaultCookie
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.DeviceController
import space.sentinel.controller.SentinelController
import space.sentinel.controller.UserProfileController
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import  space.sentinel.server.`acceptance-test`.rest.*
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester

class UserProfileReadTest : AcceptanceTest() {

    @Test
    fun `user should able fetch its profile`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth("eyboss")
                .withQuery("/1")
                .uri(UserProfileController.CONTROLLER_PATH)

        val response = GetRequester(request).get()

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->
                    MatcherAssert.assertThat(json, JsonPathMatchers.isJson())
                    MatcherAssert.assertThat(json, JsonPathMatchers.hasJsonPath("$.id", CoreMatchers.equalTo("1")))
                    MatcherAssert.assertThat(json, JsonPathMatchers.hasJsonPath("$.email", CoreMatchers.equalTo("test@elek.bl")))
                    MatcherAssert.assertThat(json, JsonPathMatchers.hasNoJsonPath("$.password"))
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `user should not be able fetch other users profile`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth("eyboss")
                .withQuery("/2")
                .uri(UserProfileController.CONTROLLER_PATH)

        val response = GetRequester(request).statusCode()

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

    @Test
    fun `user should not be able fetch without sessionid`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withQuery("/2")
                .uri(UserProfileController.CONTROLLER_PATH)

        val response = GetRequester(request).statusCode()

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

}


