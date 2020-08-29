package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http.HttpResponseStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.Notifications
import space.sentinel.api.response.NotificationsResponse
import space.sentinel.api.response.ServerErrorResponse
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator

class NotificationController @Inject constructor(private val notificationService: NotificationService,
                                                 private val translator: NotificationTranslator,
                                                 apiKeyRepository: ApiKeyRepository) : SentinelController(apiKeyRepository) {

    companion object {
        const val CONTROLLER_PATH = "notification"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes
                .post("/$CONTROLLER_PATH") { request, response ->
                    withValidApiKey(request, response) { post(request, response) }
                }
                .get("/$CONTROLLER_PATH") { request, response ->
                    withValidApiKey(request, response) { get(request, response) }
                }
    }

    private fun get(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val notifications = notificationService
                .getAll()
                .collectList()
                .map { Notifications(it) }
                .map { translator.translate(it)  }
                .doOnNext(::println)

        return response
                .status(HttpResponseStatus.OK)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .sendString(notifications)
                .then()
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
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .sendString(result)
                .then()
    }

}