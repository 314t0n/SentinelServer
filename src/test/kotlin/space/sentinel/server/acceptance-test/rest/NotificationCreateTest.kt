package space.sentinel.server.`acceptance-test`.rest


import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class NotificationCreateTest : AcceptanceTest() {

    @Test
    fun `POST alert should return empty body`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithImage)

        val response = post(requestString, NotificationController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectComplete()
                .verify()
    }

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

}