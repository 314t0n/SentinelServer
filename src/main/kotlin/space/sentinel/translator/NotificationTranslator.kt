package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import io.r2dbc.spi.Row
import reactor.core.publisher.Mono
import space.sentinel.api.Notification
import space.sentinel.api.NotificationType
import space.sentinel.api.Notifications
import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.response.NotificationResponse
import space.sentinel.api.response.ServerErrorResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NotificationTranslator @Inject constructor(private val mapper: ObjectMapper,
                                                 private val dateTimeTranslator: DateTimeTranslator) {

    fun translateRequest(request: String): Mono<NotificationRequest> {
        return Mono.just(mapper.readValue<NotificationRequest>(request))
    }

    fun translate(response: Notifications): String {
        return mapper.writeValueAsString(response)
    }

    fun translateError(error: ServerErrorResponse): Mono<String> {
        return Mono.just(mapper.writeValueAsString(error))
    }

    // useful

    fun translate(row: Row) = Notification(
            id = row.get("id", String::class.java),
            created = dateTimeTranslator.toDateTime(row),
            deviceId = row.get("device_id", String::class.java),
            message = row.get("message", String::class.java),
            type = NotificationType.INFO.toString(),
            image = ""
    )

}