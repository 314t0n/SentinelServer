package space.sentinel.service

import reactor.core.publisher.Mono
import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import java.time.OffsetDateTime

class NotificationService() {

    fun save(notification: Mono<NotificationRequest>): Mono<NotificationResponse> {
        return Mono.just(NotificationResponse(databaseId = "dbid1", modified = OffsetDateTime.now()))
    }

}