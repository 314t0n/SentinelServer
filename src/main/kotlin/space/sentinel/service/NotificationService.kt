package space.sentinel.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import reactor.core.publisher.Mono
import space.sentinel.domain.Notification
import space.sentinel.translator.NotificationTranslator

data class NotificationResponse(val message: String)

class NotificationService() {

    fun save(notification: Mono<Notification>): Mono<NotificationResponse> {
        return Mono.just(NotificationResponse("everything ok"))
    }

}