package space.sentinel.controller

import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes
import com.google.inject.Inject
import space.sentinel.repository.ApiKeyRepository

class ActuatorController @Inject constructor(apiKeyRepository: ApiKeyRepository) : SentinelController(apiKeyRepository) {

    private val pingRoute = "/ping"
    private val pingResponse = "pong"

    fun register(routes: HttpServerRoutes) {
        routes
                .get(pingRoute) { _, response ->
                    response.sendString(Mono.just(pingResponse))
                }
    }

}