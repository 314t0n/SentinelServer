package space.sentinel.api.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import space.sentinel.api.NotificationType
import java.time.OffsetDateTime
import java.util.*

open class DeviceRequest @JsonCreator constructor(
        @param:JsonProperty("api_key") val apiKey: String,
        @param:JsonProperty("name") val name: String)