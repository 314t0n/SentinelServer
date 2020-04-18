package space.sentinel.repository

import reactor.core.publisher.Mono
import space.sentinel.repository.entity.NotificationEntity

class InMemoryCache {

    fun cache(notification: NotificationEntity): Mono<NotificationEntity> {
        return Mono.just(notification)
    }

}