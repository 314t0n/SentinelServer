package space.sentinel.server

import com.google.inject.Injector
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.getInstance
import io.netty.channel.ChannelOption
import reactor.core.publisher.Mono
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController

/**
 * HTTP Server setup
 */
class SentinelServer() {
    fun create(injector: Injector): DisposableServer {
        return HttpServer.create()
                .tcpConfiguration { tcpServer ->
                    tcpServer.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                }
                .port(injector.getInstance<Config>().getInt("port"))
                .wiretap(false)

                .route { routes: HttpServerRoutes ->
                    injector.getInstance<ActuatorController>().register(routes)
                    injector.getInstance<NotificationController>().register(routes)
                }
                .bindNow()
    }
}