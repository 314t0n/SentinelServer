package space.sentinel.repository

import reactor.core.publisher.Mono
import space.sentinel.repository.entity.NotificationEntity

class InMemoryRepository {

    fun save(img: ImageEntity, filename: String): ImageEntity {
        return img
    }

    fun save(notification: NotificationEntity): Mono<NotificationEntity> {
        return Mono.just(notification)
    }

}