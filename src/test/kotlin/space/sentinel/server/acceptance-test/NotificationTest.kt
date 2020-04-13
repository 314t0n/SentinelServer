package space.sentinel.server.`acceptance-test`

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController

class NotificationTest : AcceptanceTest() {

    @Test
    fun `notification POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.ANotificationRequest)

        val response = client.post()
                .uri(serverUrl(NotificationController.CONTROLLER_PATH))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent().retain().aggregate()
                .asString()

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

}