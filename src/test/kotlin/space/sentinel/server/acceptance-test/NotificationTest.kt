package space.sentinel.server.`acceptance-test`

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController

class NotificationTest : AcceptanceTest() {

    @Test
    fun `ALERT with image POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.AlertNotificationWithImage)

        val response = fetch(requestString).doOnNext(::println)

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest).contains("databaseId")
                    assertThat(underTest).contains("modified")
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `ALERT without image POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.AlertNotificationWithoutImage)

        val response = fetch(requestString).doOnNext(::println)

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest).contains("databaseId")
                    assertThat(underTest).contains("modified")
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `INFO POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.InfoNotification)

        val response = fetch(requestString)

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest).contains("databaseId")
                    assertThat(underTest).contains("modified")
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `Malformed JSON POST should response with Bad Request`() {
        val requestString = "invalid request"

        val response = fetch(requestString)

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest).contains("\"errorCode\":400,\"reason\":\"Unrecognized token")
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `Unauthorized with wrong Api Key`() {
        val response = client
                .headers { h -> h.set("x-sentinel-api-key", "test123").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(objectmapper.writeValueAsString(DomainObjects.InfoNotification))))
                .response().map { it.status() }

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest.code()).isEqualTo(401)
                    true
                }
                .expectComplete()
                .verify()
    }


    @Test
    fun `Unauthorized without Api Key`() {
        val response = client
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(objectmapper.writeValueAsString(DomainObjects.InfoNotification))))
                .response().map { it.status() }

        StepVerifier
                .create(response)
                .expectNextMatches { underTest ->
                    assertThat(underTest.code()).isEqualTo(401)
                    true
                }
                .expectComplete()
                .verify()
    }

    private fun fetch(requestString: String): Mono<String> {
        return client
                .headers { h -> h.set("x-sentinel-api-key", "test").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent()
                .retain().aggregate()
                .asString()
    }

}