package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import space.sentinel.domain.Notification
import space.sentinel.service.NotificationResponse

class NotificationTranslator(private val objectmapper: ObjectMapper) {

    private  val logger: Logger = LoggerFactory.getLogger("NotificationTranslator")

    fun translate(input: String): Mono<Notification> {
        logger.info(input)
        return Mono.just(objectmapper.readValue<Notification>(input))
    }

    fun translate(resp: Mono<NotificationResponse>): Mono<String> {
        return resp.map {objectmapper.writeValueAsString(it)}
    }

}