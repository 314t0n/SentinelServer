package space.sentinel.controller

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.cookie.Cookie
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import space.sentinel.api.UserProfile
import space.sentinel.api.response.ServerErrorResponse
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.UserService
import java.lang.IllegalArgumentException
import java.util.*

abstract class SentinelController @Inject constructor(private val apiKeyRepository: ApiKeyRepository,
                                                      private val userService: UserService) {

    companion object {
        const val API_KEY_HEADER = "x-sentinel-api-key"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    protected fun withValidApiKey(request: HttpServerRequest, response: HttpServerResponse, requestHandler: () -> Mono<Void>): Publisher<Void> {
        val apiKey = Optional.ofNullable(request.requestHeaders().get(API_KEY_HEADER))

        return if (apiKey.isPresent && apiKeyRepository.isValid(apiKey.get())) {
            requestHandler()
        } else {
            unauthorized(response)
        }
    }

    protected fun withUserProfile(request: HttpServerRequest): Mono<UserProfile> {
        return Mono.fromCallable { request.cookies()["session_id"] }
                .filter { !it.isNullOrEmpty<Cookie?>() }
                .map<Optional<String>> { sessionId -> sessionId!!.stream().filter { it.name() == "session_id" }.map { it.value() }.findAny() }
                .filter { it.isPresent }
                .flatMap { sessionId ->
                    userService.userBySessionId(sessionId.get())
                }
                .switchIfEmpty(Mono.error(IllegalArgumentException()))
    }

    protected fun unauthorized(response: HttpServerResponse) =
            response
                    .status(HttpResponseStatus.UNAUTHORIZED)
                    .sendString(Mono.just("unauthorized"))
                    .then()
                    .doOnNext { logger.warn("Unauthorized attempt!") }

}