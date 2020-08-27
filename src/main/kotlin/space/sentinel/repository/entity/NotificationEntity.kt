package space.sentinel.repository.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import space.sentinel.api.NotificationType
import java.time.OffsetDateTime
import java.util.*

open class NotificationEntity @JsonCreator constructor(
        @param:JsonProperty("timestamp") val timestamp: OffsetDateTime,
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("deviceId") val deviceId: String,
        @param:JsonProperty("message") val message: String,
        @param:JsonProperty("type") val type: NotificationType,
        @param:JsonProperty("filename") val filename: String,
        @param:JsonProperty("image") val image: Optional<ByteArray>)