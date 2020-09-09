package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http.HttpResponseStatus.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.Notifications
import space.sentinel.api.response.ServerErrorResponse
import space.sentinel.exception.UnauthorizedException
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.NotificationService
import space.sentinel.service.UserService
import space.sentinel.translator.NotificationTranslator
import space.sentinel.util.QueryParameterResolver
import java.lang.IllegalArgumentException

class NotificationController @Inject constructor(private val notificationService: NotificationService,
                                                 private val translator: NotificationTranslator,
                                                 private val queryParameterResolver: QueryParameterResolver,
                                                 userService: UserService,
                                                 apiKeyRepository: ApiKeyRepository) : SentinelController(apiKeyRepository, userService) {

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
                    withValidApiKey(request, response) { getAll(request, response) }
                }
                .get("/${CONTROLLER_PATH}/{id}") { request, response ->
                    withValidApiKey(request, response) { get(request, response) }
                }
    }

    private fun get(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val userProfile = withUserProfile(request)
        return notificationService
                .get(request.param("id")!!, userProfile)
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
                            .status(NOT_FOUND)
                            .send()
                            .then()
                }.onErrorResume(UnauthorizedException::class.java) {
                    unauthorized(response)
                }
                .then()
    }

    private fun getAll(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val notifications = notificationService
                .getAll(queryParameterResolver.parameterMap(request))
                .collectList()
                .map { Notifications(it) }
                .map { translator.translate(it) }
                .doOnNext(::println)

        return response
                .status(OK)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .sendString(notifications)
                .then()
    }

    private fun post(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        return request
                .receive()
                .aggregate()
                .asString()
                .map(translator::translateRequest)
                .map { notificationService.save(it) }
                .flatMap {
                    val entityId = it.map { id -> translator.translateId(id) }
                    response
                            .status(CREATED)
                            .sendString(entityId)
                            .then()
                }
                .onErrorResume(JsonParseException::class.java) {
                    response
                            .status(BAD_REQUEST)
                            .sendString(translator.translateError(ServerErrorResponse.createErrorResponse(it)))
                            .then()
                }
                .onErrorResume(Exception::class.java) {
                    response
                            .status(INTERNAL_SERVER_ERROR)
                            .sendString(translator.translateError(ServerErrorResponse.createErrorResponse(it)))
                            .then()
                }
                .doOnError { logger.error("Error while saving notification: ${it.message}") }
                .then()
    }

}