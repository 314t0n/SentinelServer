package space.sentinel.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator
import space.sentinel.util.ConfigLoaderFactory

class SentinelServer() {
    fun create(wiring: Wiring): DisposableServer {

        val server = HttpServer.create()
                .port(wiring.config.getInt("port"))
                .wiretap(false)
                .route { routes: HttpServerRoutes ->
                    wiring.actuatorController.register(routes)
                    wiring.notificationController.register(routes)
                }
                .bindNow()
        return server
    }
}