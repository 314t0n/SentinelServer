package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonpath.matchers.JsonPathMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.api.EntityId
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.AlertNotificationWithImage
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.AlertNotificationWithoutImage
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.InfoNotification
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class NotificationCreateTest : AcceptanceTest() {

    @Test
    fun `POST alert should respond CREATED`() {
        val requestString = mapper.writeValueAsString(AlertNotificationWithImage)
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST alert without image should respond Ok`() {
        val requestString = mapper.writeValueAsString(AlertNotificationWithoutImage)
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST info should respond with Ok`() {
        val requestString = mapper.writeValueAsString(InfoNotification)
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST should create new entity`() {
        val requestString = mapper.writeValueAsString(InfoNotification)
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .uri(CONTROLLER_PATH)
        val postResponse = PostRequester(request)
                .post(requestString).block()
        val entityId = mapper.readValue(postResponse, EntityId::class.java)

        val getRequest = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .withQuery("/${entityId.id}")
                .uri(CONTROLLER_PATH)
        val getResponse = GetRequester(getRequest).get()

        StepVerifier
                .create(getResponse)
                .expectNextMatches { json: String ->
                    MatcherAssert.assertThat(json, JsonPathMatchers.isJson())
                    MatcherAssert.assertThat(json, JsonPathMatchers.hasJsonPath("$.id", CoreMatchers.equalTo(entityId.id.toString())))
                    true
                }
                .expectComplete()
                .verify()
    }

}