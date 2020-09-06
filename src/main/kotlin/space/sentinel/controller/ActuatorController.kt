package space.sentinel.controller

import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes
import com.google.inject.Inject
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.UserService

class ActuatorController @Inject constructor(apiKeyRepository: ApiKeyRepository,
                                             userService: UserService) : SentinelController(apiKeyRepository, userService) {

    private val pingRoute = "/ping"
    private val pingResponse = "pong"

    fun register(routes: HttpServerRoutes) {
        routes
                .get(pingRoute) { _, response ->
                    response.sendString(Mono.just(pingResponse))
                }
    }

}