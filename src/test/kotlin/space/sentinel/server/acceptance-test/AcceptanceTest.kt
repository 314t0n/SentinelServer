package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Guice
import com.google.inject.Injector
import dev.misfitlabs.kotlinguice4.getInstance
import io.netty.handler.codec.http.cookie.DefaultCookie
import org.apache.ibatis.jdbc.ScriptRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient
import space.sentinel.controller.SentinelController
import space.sentinel.server.SentinelServer
import space.sentinel.server.modules.SentinelServerModule
import java.io.InputStreamReader
import java.sql.DriverManager

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AcceptanceTest {
    protected val injector: Injector = Guice.createInjector(SentinelServerModule())
    protected val server = SentinelServer().create(injector)
    protected val client: HttpClient = HttpClient.create()
    protected val baseUri = "http://${server.host()}:${server.port()}"
    protected val mapper = injector.getInstance<ObjectMapper>()

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

}