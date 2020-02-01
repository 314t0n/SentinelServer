package space.sentinel.controller

import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes

class ActuatorController() {

    private val pingRoute = "/ping"
    private val pingResponse = "pong"

    fun register(routes: HttpServerRoutes) {
        routes
                .get(pingRoute) { _, response ->
                    response.sendString(Mono.just(pingResponse))
                }
    }

}