package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import reactor.netty.http.client.HttpClient
import space.sentinel.server.SentinelServer
import space.sentinel.server.Wiring

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AcceptanceTest {

    protected val wiring = Wiring("acceptance")
    protected val server = SentinelServer().create(wiring)
    protected val client = HttpClient.create()
    protected val baseUri = "http://${server.host()}:${server.port()}"
    protected val objectmapper = wiring.objectmapper

    fun serverUrl(path: String): String {
        return "$baseUri/$path"
    }

    @AfterAll
    internal fun destroy() {
        server.disposeNow()
    }

}