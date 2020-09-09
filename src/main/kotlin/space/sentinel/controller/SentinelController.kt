package space.sentinel.controller

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import io.netty.handler.codec.http.cookie.Cookie
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import space.sentinel.api.Device
import space.sentinel.api.UserProfile
import space.sentinel.exception.UnauthorizedException
import space.sentinel.service.ApiKeyService
import space.sentinel.service.UserService
import java.util.*

abstract class SentinelController @Inject constructor(private val apiKeyService: ApiKeyService,
                                                      private val userService: UserService) {

    companion object {
        const val API_KEY_HEADER = "x-sentinel-api-key"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Returns Device by given API key
     *
     * @param request
     * @param response
     * @exception UnauthorizedException if api key not found or not provided
     */
    protected fun deviceByApiKey(request: HttpServerRequest): Mono<Device> {
        val apiKey = Optional.ofNullable(request.requestHeaders().get(API_KEY_HEADER))
        return apiKeyService.deviceByApiKey(apiKey).doOnError { logger.warn(it.message) }
    }

    /**
     * Returns user profile from database, TODO move
     */
    protected fun withUserProfile(request: HttpServerRequest): Mono<UserProfile> {
        return sessionFromRequest(request)
                .flatMap { sessionId -> userService.userBySessionId(sessionId.get()) }
                .switchIfEmpty(Mono.error(UnauthorizedException()))
    }

    /**
     * Returns user session from request, TODO move
     */
    protected fun withSessionId(request: HttpServerRequest): Mono<String> {
        return sessionFromRequest(request)
                .map { it.get() }
                .switchIfEmpty(Mono.error(UnauthorizedException()))
    }

    private fun sessionFromRequest(request: HttpServerRequest): Mono<Optional<String>> {
        return Mono.fromCallable { request.cookies()["session_id"] }
                .filter { !it.isNullOrEmpty<Cookie?>() }
                .map<Optional<String>> { sessionId -> sessionId!!.stream().filter { it.name() == "session_id" }.map { it.value() }.findAny() }
                .filter { it.isPresent }
    }

    /**
     * Returns HTTP Status Code 401
     */
    protected fun unauthorized(response: HttpServerResponse): Mono<Void> =
            response
                    .status(HttpResponseStatus.UNAUTHORIZED)
                    .sendString(Mono.just("unauthorized"))
                    .then()
                    .doOnNext { logger.warn("Unauthorized attempt!") }

    /**
     * Returns HTTP Status Code 500 with error message
     */
    protected fun internalServerError(response: HttpServerResponse, resp: Mono<String>): Mono<Void> {
        return response
                .status(INTERNAL_SERVER_ERROR)
                .sendString(resp)
                .then()
    }

    /**
     * Returns HTTP Status Code 400 with error message
     */
    protected fun badRequest(response: HttpServerResponse, resp: Mono<String>): Mono<Void> {
        return response
                .status(BAD_REQUEST)
                .sendString(resp)
                .then()
    }
}