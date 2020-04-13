package space.sentinel.server.`acceptance-test`

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.netty.ByteBufFlux
import reactor.test.StepVerifier

class NotificationTest : AcceptanceTest() {

    @Test
    fun `notification POST should response with OK`() {
        val requestString = objectmapper.writeValueAsString(DomainObjects.AMotionDetectAlert)

        val response = client.post()
                .uri(serverUrl("notification"))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent()
                .asString()
                .doOnNext{
                    println("--------------------")
                    println(it) }

        StepVerifier
                .create(response)
                .expectNext("meesage")
                .expectComplete()
                .verify()

    }

}