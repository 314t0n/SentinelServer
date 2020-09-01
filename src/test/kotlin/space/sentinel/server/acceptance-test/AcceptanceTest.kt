package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Guice
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient
import space.sentinel.controller.DeviceController
import space.sentinel.controller.SentinelController
import space.sentinel.server.SentinelServer
import space.sentinel.server.modules.SentinelServerModule
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AcceptanceTest {

    protected val injector: Injector = Guice.createInjector(SentinelServerModule())
    protected val server = SentinelServer().create(injector)
    protected val client = HttpClient.create()
    protected val baseUri = "http://${server.host()}:${server.port()}"
    protected val mapper = injector.getInstance<ObjectMapper>()

    fun serverUrl(path: String): String {
        return "$baseUri/$path"
    }

    @BeforeAll
    internal fun setup() {
        val conn = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", "testelek")
        val resource = this.javaClass.classLoader.getResource("testdb.sql")

        conn.use {
            val runner = ScriptRunner(conn)
            val reader = InputStreamReader(resource!!.openStream())
            reader.use {
                runner.runScript(reader)
            }
        }
    }


    @AfterAll
    internal fun destroy() {
        server.disposeNow()
    }

    protected fun get(requestUri: String, query: String = ""): Mono<String> {
        return client
                .headers { h -> h.set(SentinelController.API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .get()
                .uri("""${serverUrl(requestUri)}$query""")
                .responseContent()
                .retain().aggregate()
                .asString()
    }

    protected fun post(requestString: String, path: String): Mono<String> {
        return client
                .headers { h -> h.set(SentinelController.API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(path))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .responseContent()
                .retain().aggregate()
                .asString()
    }

    protected fun statusCode(requestString: String, path: String): Mono<Int> {
        return client
                .headers { h -> h.set(SentinelController.API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .post()
                .uri(serverUrl(path))
                .send(ByteBufFlux.fromString(Flux.just(requestString)))
                .response()
                .map { it.status().code() }
    }

    protected fun statusCode(path: String): Mono<Int> {
        return client
                .headers { h -> h.set(SentinelController.API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .get()
                .uri(serverUrl(path))
                .response()
                .map { it.status().code() }
    }

}