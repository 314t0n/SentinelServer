package space.sentinel.controller

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import space.sentinel.repository.ApiKeyRepository
import java.util.*

abstract class SentinelController @Inject constructor(private val apiKeyRepository: ApiKeyRepository) {

    companion object{
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

    private fun unauthorized(response: HttpServerResponse) =
            response
                    .status(HttpResponseStatus.UNAUTHORIZED)
                    .send()
                    .then()
                    .doOnNext { logger.warn("Unauthorized attempt!") }

}