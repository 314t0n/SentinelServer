package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

open class Notification @JsonCreator constructor(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("created") val created: OffsetDateTime,
        @param:JsonProperty("message") val message: String,
        @param:JsonProperty("device_id") val deviceId: String,
        @param:JsonProperty("image_base64") val image: String,
        @param:JsonProperty("notification_type") val type: String
)