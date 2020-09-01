package space.sentinel.server.`acceptance-test`.rest


import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController
import space.sentinel.controller.SentinelController.Companion.API_KEY_HEADER
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class NotificationCreateTest : AcceptanceTest() {

    @Test
    fun `POST alert should return empty body`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithImage)

        val response = post(requestString)

        StepVerifier
                .create(response)
                .expectComplete()
                .verify()
    }

    @Test
    fun `POST alert should respond CREATED`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithImage)

        val response = statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST alert without image should respond Ok`() {
        val requestString = mapper.writeValueAsString(DomainObjects.AlertNotificationWithoutImage)

        val response = statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST info should respond with Ok`() {
        val requestString = mapper.writeValueAsString(DomainObjects.InfoNotification)

        val response = statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }


    private fun post(requestString: String): Mono<String> {
        return client
                .headers { h -> h.set(API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent()
                .retain().aggregate()
                .asString()
    }

    private fun statusCode(requestString: String): Mono<Int> {
        return client
                .headers { h -> h.set(API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .response()
                .map { it.status().code() }
    }


}