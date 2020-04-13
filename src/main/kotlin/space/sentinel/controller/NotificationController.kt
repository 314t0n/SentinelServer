package space.sentinel.controller

import io.netty.handler.codec.http.HttpResponseStatus
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

            val result = request
                    .receive()
                    .aggregate()
                    .asString()
                    .map { str: String -> translator.translate(str) }
                    .map { notificationService.save(it) }
                    .flatMap { re: Mono<NotificationResponse> -> translator.translate(re) }

            response.sendString(result).then()

        }
    }

//    fun register(routes: HttpServerRoutes) {
//        routes.post(notificationRoute) { request, response ->
//
//            request
//                    .receive()
//                    .retain()
//                    .aggregate()
//                    .asString()
//                    .map { str: String -> translator.translate(str) }
//                    .map { notificationService.save(it) }
//                    .map { re: Mono<NotificationResponse> -> translator.translate(re) }
//                    .doOnNext { eze ->
//                        response.status(200).sendString(eze).then()
//                    }
//                    .doOnError { logger.error(it.message, it) }
//                    .onErrorMap {
//                        SentinelError("error")
//                    }
//                    .doOnNext { eze ->
//                        response.status(500).then()
//                    }
//                    .then()
////                    .then(response.status(HttpResponseStatus.OK).sendString(Mono.just("Faszom")).then())
//
//        }
//    }

}