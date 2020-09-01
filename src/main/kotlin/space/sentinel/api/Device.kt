package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

open class Device @JsonCreator constructor(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("created") val created: OffsetDateTime,
        @param:JsonProperty("api_key") val apiKey: String,
        @param:JsonProperty("name") val name: String
)