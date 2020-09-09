package space.sentinel.controller

import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes
import com.google.inject.Inject
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.ApiKeyService
import space.sentinel.service.UserService

class ActuatorController @Inject constructor(apiKeyService: ApiKeyService,
                                             userService: UserService) : SentinelController(apiKeyService, userService) {

    private val pingRoute = "/ping"
    private val pingResponse = "pong"

    fun register(routes: HttpServerRoutes) {
        routes
                .get(pingRoute) { _, response ->
                    response.sendString(Mono.just(pingResponse))
                }
    }

}