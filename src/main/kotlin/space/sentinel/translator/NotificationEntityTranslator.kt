package space.sentinel.translator

import reactor.core.publisher.Mono
import space.sentinel.api.Notification
import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.response.NotificationResponse
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

    fun trasnalte(e: NotificationEntity): NotificationResponse {
        return NotificationResponse(modified = e.timestamp, databaseId = e.id)
    }

    fun trasnalte2(e: NotificationEntity): Notification {
        return Notification(
                id = e.id,
                created = e.timestamp,
                deviceId = e.deviceId,
                message = e.message,
                type = e.type.toString(),
                image = ""
        )
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