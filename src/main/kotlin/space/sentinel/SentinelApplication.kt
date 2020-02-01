package space.sentinel

import org.slf4j.LoggerFactory
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController
import space.sentinel.util.ConfigLoaderFactory

class SentinelApplication() {
    fun start(env: String): DisposableServer {
        val config = ConfigLoaderFactory().load(env)

        val server = HttpServer.create()
                .port(config.getInt("port"))
                .route { routes: HttpServerRoutes ->
                    ActuatorController().register(routes)
                    NotificationController().register(routes)
                }
                .bindNow()
        return server
    }
}