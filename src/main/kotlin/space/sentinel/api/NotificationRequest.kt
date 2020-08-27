package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

open class NotificationRequest @JsonCreator constructor(
        @param:JsonProperty("timestamp") val timestamp: OffsetDateTime,
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("deviceId") val deviceId: String,
        @param:JsonProperty("message") val message: String,
        @param:JsonProperty("type") val type: NotificationType,
        @param:JsonProperty("image") val image: Optional<ByteArray>)