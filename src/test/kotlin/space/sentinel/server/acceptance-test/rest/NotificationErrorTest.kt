package space.sentinel.server.`acceptance-test`.rest

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.controller.SentinelController.Companion.API_KEY_HEADER
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class NotificationErrorTest : AcceptanceTest() {

    @Test
    fun `Malformed JSON POST should response with Bad Request`() {
        val requestString = "invalid request"

        val response = statusCode(requestString, CONTROLLER_PATH)

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
                .uri(serverUrl(CONTROLLER_PATH))
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
                .uri(serverUrl(CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(mapper.writeValueAsString(DomainObjects.InfoNotification))))
                .response().map { it.status().code() }

        StepVerifier
                .create(response)
                .expectNext(401)
                .verifyComplete()
    }

}