package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.ServerErrorResponse
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator

class NotificationController @Inject constructor(private val notificationService: NotificationService,
                                                 private val translator: NotificationTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes.post("/$CONTROLLER_PATH") { request, response ->

            val fsz = Mono.just<String>("gec")

            val result = request
                    .receive()
                    .aggregate()
                    .asString()
                    .map(translator::translateRequest)
                    .map(notificationService::save)
                    .flatMap(translator::translateResponse)
                    .onErrorResume(JsonParseException::class.java) {
                        translator.translateError(ServerErrorResponse(HttpResponseStatus.BAD_REQUEST.code(), it.message.orEmpty()))
                    }
                    .doOnError { logger.error(it.message, it) }

            response.sendString(result).then()
        }
    }

    companion object {
        const val CONTROLLER_PATH = "notification"
    }

}