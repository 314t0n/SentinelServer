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
import space.sentinel.api.Devices
import space.sentinel.api.response.ServerErrorResponse
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.ApiKeyService
import space.sentinel.service.DeviceService
import space.sentinel.service.UserService
import space.sentinel.translator.DeviceTranslator
import space.sentinel.util.QueryParameterResolver
import java.lang.IllegalArgumentException

class DeviceController @Inject constructor(private val deviceService: DeviceService,
                                           private val deviceTranslator: DeviceTranslator,
                                           private val queryParameterResolver: QueryParameterResolver,
                                           userService: UserService,
                                           apiKeyService: ApiKeyService) : SentinelController(apiKeyService, userService) {

    companion object {
        const val CONTROLLER_PATH = "device"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes
                .post("/$CONTROLLER_PATH") { request, response ->
                    post(request, response)
                }
                .get("/$CONTROLLER_PATH") { request, response ->
                    getAll(request, response)
                }
                .get("/$CONTROLLER_PATH/{id}") { request, response ->
                    get(request, response)
                }
    }

    private fun get(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val id = request.param("id")!!

        return deviceService
                .get(id)
                .map { deviceTranslator.toJson(it) }
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
                }
                .then()
    }

    private fun getAll(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val devices = deviceService
                .getAll(queryParameterResolver.parameterMap(request))
                .collectList()
                .map { Devices(it) }
                .map { deviceTranslator.toJson(it) }

        return response
                .status(OK)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .sendString(devices)
                .then()
    }

    private fun post(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        val deviceCreateRequest = request
                .receive()
                .aggregate()
                .asString()
                .map { deviceTranslator.translate(it) }

        return withUserProfile(request)
                .zipWith(deviceCreateRequest)
                .map { deviceService.save(it.t2, it.t1) }
                .flatMap {
                    val entityId = it.map { id -> deviceTranslator.translateId(id) }
                    response
                            .status(CREATED)
                            .sendString(entityId)
                            .then()
                }
                .onErrorResume(IllegalArgumentException::class.java) {
                    unauthorized(response)
                }
                .onErrorResume(JsonParseException::class.java) {
                    logger.warn("Error while creating device: ${it.message}")
                    badRequest(response, deviceTranslator.translateError(ServerErrorResponse.createErrorResponse(it)))
                }
                .onErrorResume(Exception::class.java) {
                    logger.error("Error while creating device: ${it.message}", it)
                    internalServerError(response, deviceTranslator.translateError(ServerErrorResponse.createErrorResponse(it)))
                }
                .doOnError { logger.error("Error while creating device: ${it.message}") }
                .then()
    }

}