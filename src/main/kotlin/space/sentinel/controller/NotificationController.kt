package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.ServerErrorResponse
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator
import java.util.*

class NotificationController @Inject constructor(private val notificationService: NotificationService,
                                                 private val translator: NotificationTranslator,
                                                 apiKeyRepository: ApiKeyRepository) : SentinelController(apiKeyRepository) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes.post("/$CONTROLLER_PATH") { request, response ->
            withValidApiKey(request, response) { post(request, response) }
        }
    }


    private fun post(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val result = request
                .receive()
                .aggregate()
                .asString()
                .map(translator::translateRequest)
                .map(notificationService::save)
                .flatMap(translator::translateResponse)
                .doOnError { logger.error(it.message, it) }
                .onErrorResume(JsonParseException::class.java) {
                    translator.translateError(ServerErrorResponse.createBadRequest(it))
                }
                .onErrorResume(Exception::class.java) {
                    translator.translateError(ServerErrorResponse.createInternalServerError(it))
                }

        return response
                .status(HttpResponseStatus.OK)
                .header("Content-type", "application/json")
                .sendString(result)
                .then()
    }

    companion object {
        const val CONTROLLER_PATH = "notification"
    }

}