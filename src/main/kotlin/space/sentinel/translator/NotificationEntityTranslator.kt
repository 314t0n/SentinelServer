package space.sentinel.translator

import reactor.core.publisher.Mono
import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import space.sentinel.repository.ImageEntity
import space.sentinel.repository.entity.NotificationEntity
import java.time.OffsetDateTime

class NotificationEntityTranslator {

    fun translateToImageEntity(request: NotificationRequest): ImageEntity {
        return ImageEntity(id = request.id, bytes = request.image.get(), date = request.timestamp)
    }

    fun translateToEntity(req: NotificationRequest, fileName: Mono<String>): Mono<NotificationEntity> {
        return fileName.map { filename -> translateNotificationEntity(req, filename) }
    }

    private fun translateNotificationEntity(request: NotificationRequest, filename: String): NotificationEntity {
        return NotificationEntity(timestamp = request.timestamp,
                id = request.id,
                deviceId = request.deviceId,
                message = request.message,
                type = request.type,
                filename = filename,
                image = request.image)
    }

    fun translateToResponse(e: Mono<String>): Mono<NotificationResponse> {
        return e.map { databaseId -> NotificationResponse(databaseId = databaseId, modified = OffsetDateTime.now()) }
    }

}