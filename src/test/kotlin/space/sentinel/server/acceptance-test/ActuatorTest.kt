package space.sentinel.server.`acceptance-test`

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class ActuatorTest: AcceptanceTest(){

    @Test
    fun `ping endpoint should resposne with pong`() {
        val response = client.get()
                .uri(serverUrl("ping"))
                .responseContent()
                .asString()

        StepVerifier
                .create(response)
                .expectNext("pong")
                .expectComplete()
                .verify()
    }

}