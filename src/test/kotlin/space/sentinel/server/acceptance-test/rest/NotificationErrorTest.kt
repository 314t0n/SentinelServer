package space.sentinel.server.`acceptance-test`.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController
import space.sentinel.controller.SentinelController.Companion.API_KEY_HEADER
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class NotificationErrorTest : AcceptanceTest() {

    @Test
    fun `Malformed JSON POST should response with Bad Request`() {
        val requestString = "invalid request"

        val response = statusCode(requestString)

        StepVerifier
                .create(response)
                .expectNext(400)
                .verifyComplete()
    }

    @Test
    fun `Unauthorized with wrong Api Key`() {
        val response = client
                .headers { h -> h.set(API_KEY_HEADER, "test123").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(mapper.writeValueAsString(DomainObjects.InfoNotification))))
                .response().map { it.status().code() }

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }


    @Test
    fun `Unauthorized without Api Key`() {
        val response = client
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(mapper.writeValueAsString(DomainObjects.InfoNotification))))
                .response().map { it.status().code() }

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
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