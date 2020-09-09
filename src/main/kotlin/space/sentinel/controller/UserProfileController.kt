package space.sentinel.controller

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.exception.UnauthorizedException
import space.sentinel.service.ApiKeyService
import space.sentinel.service.UserService
import space.sentinel.translator.UserProfileTranslator
import space.sentinel.util.QueryParameterResolver

class UserProfileController @Inject constructor(private val translator: UserProfileTranslator,
                                                userService: UserService,
                                                apiKeyService: ApiKeyService) : SentinelController(apiKeyService, userService) {

    companion object {
        const val CONTROLLER_PATH = "user"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes
                .get("/${CONTROLLER_PATH}/{id}") { request, response -> get(request, response) }
    }

    private fun get(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {

        return withUserProfile(request)
                .filter { it.id == request.param("id")!! }
                .switchIfEmpty(Mono.error(IllegalArgumentException()))
                .map { translator.toJson(it) }
                .flatMap {
                    response
                            .status(OK)
                            .header(CONTENT_TYPE, APPLICATION_JSON)
                            .sendString(Mono.just(it))
                            .then()
                }
                .onErrorResume(IllegalArgumentException::class.java) {
                    response
                            .status(UNAUTHORIZED)
                            .send()
                            .then()
                }.onErrorResume(UnauthorizedException::class.java) {
                    unauthorized(response)
                }
    }

}