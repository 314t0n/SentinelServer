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
    fun `notification POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.ANotificationRequest)

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
    fun `notification POST should response with Bad Request`() {
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

    private fun fetch(requestString: String): Mono<String> {
        val response = client.post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent().retain().aggregate()
                .asString()
        return response
    }

}