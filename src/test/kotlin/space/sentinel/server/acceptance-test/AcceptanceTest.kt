package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Guice
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import reactor.netty.http.client.HttpClient
import space.sentinel.server.SentinelServer
import space.sentinel.server.modules.SentinelServerModule

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AcceptanceTest {

    protected val injector: Injector = Guice.createInjector(SentinelServerModule())
    protected val server = SentinelServer().create(injector)
    protected val client = HttpClient.create().headers{ h -> h.set("api-key", "test")}
    protected val baseUri = "http://${server.host()}:${server.port()}"
    protected val objectmapper = injector.getInstance<ObjectMapper>()

    fun serverUrl(path: String): String {
        return "$baseUri/$path"
    }

    @AfterAll
    internal fun destroy() {
        server.disposeNow()
    }

}