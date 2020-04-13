package space.sentinel.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.service.NotificationResponse
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator
import java.lang.RuntimeException

class SentinelError(val error: String) : RuntimeException(error)

class NotificationController(private val notificationService: NotificationService, private val translator: NotificationTranslator) {
    private val notificationRoute = "/notification"

    private val logger: Logger = LoggerFactory.getLogger("NotificationController")

    fun register(routes: HttpServerRoutes) {
        routes.post(notificationRoute) { request, response ->
            request
                    .receive()
                    .asString()
                    .map { str: String -> translator.translate(str) }
                    .map { notificationService.save(it) }
                    .map { re: Mono<NotificationResponse> -> translator.translate(re) }
                    .doOnError{ logger.error("theerror", it)}
                    .onErrorMap { SentinelError("error") }
                    .map { response.sendString(it) }
                    .then()

        }
    }

}