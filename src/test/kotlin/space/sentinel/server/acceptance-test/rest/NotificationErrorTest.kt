package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize
import com.jayway.jsonpath.matchers.JsonPathMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class NotificationErrorTest : AcceptanceTest() {

    @Test
    fun `Malformed JSON POST should response with Bad Request`() {
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode("invalid request")

        StepVerifier
                .create(response)
                .expectNext(400)
                .verifyComplete()
    }

    @Test
    fun `Unauthorized with wrong Api Key`() {
        val request = PostRequestBuilder(baseUri)
                .withApiKey("invalid")
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(DomainObjects.InfoNotification))

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

    @Test
    fun `Unauthorized without Api Key`() {
        val request = PostRequestBuilder(baseUri)
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(DomainObjects.InfoNotification))

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

    @Test
    fun `Unauthorized to read without login`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withQuery("/2")
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).statusCode()

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }
}