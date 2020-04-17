package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.ServerErrorResponse
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator

class NotificationController @Inject constructor(private val notificationService: NotificationService,
                                                 private val translator: NotificationTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes.post("/$CONTROLLER_PATH") { request, response ->

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

            response.sendString(result).then()
        }
    }

    companion object {
        const val CONTROLLER_PATH = "notification"
    }

}