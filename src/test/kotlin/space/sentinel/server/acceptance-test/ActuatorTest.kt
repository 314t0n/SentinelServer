package space.sentinel.server.`acceptance-test`

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.netty.http.client.HttpClient
import reactor.test.StepVerifier
import space.sentinel.SentinelApplication

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActuatorTest() {

    private val server = SentinelApplication().start("acceptance")
    private val client = HttpClient.create()

    @AfterAll
    internal fun destroy() {
        server.disposeNow()
    }

    @Test
    fun `ping endpoint should resposne with pong`() {
        val uri = "http://${server.host()}:${server.port()}/ping"

        val response = client.get()
                .uri(uri)
                .responseContent()
                .asString()

        StepVerifier
                .create(response)
                .expectNext("pong")
                .expectComplete()
                .verify()
    }

}