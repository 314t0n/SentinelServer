package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonpath.matchers.JsonPathMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.api.EntityId
import space.sentinel.api.request.DeviceRequest
import space.sentinel.controller.DeviceController
import space.sentinel.controller.NotificationController
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.AlertNotificationWithImage

class NotificationCreateTest : AcceptanceTest() {

    @Test
    fun `POST alert should respond CREATED`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithImage)

        val response = statusCode(requestString, NotificationController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST alert without image should respond Ok`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithoutImage)

        val response = statusCode(requestString, NotificationController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST info should respond with Ok`() {
        val requestString = mapper.writeValueAsString(DomainObjects.InfoNotification)

        val response = statusCode(requestString, NotificationController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST should create new entity`() {
        val postResponse = post(mapper.writeValueAsString(AlertNotificationWithImage), NotificationController.CONTROLLER_PATH).block()
        val entityId = mapper.readValue(postResponse, EntityId::class.java)

        val response = get(NotificationController.CONTROLLER_PATH, "/${entityId.id}")

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->
                    MatcherAssert.assertThat(json, JsonPathMatchers.isJson())
                    MatcherAssert.assertThat(json, JsonPathMatchers.hasJsonPath("$.id", CoreMatchers.equalTo(entityId.id.toString())))
                    true
                }
                .expectComplete()
                .verify()
    }

}