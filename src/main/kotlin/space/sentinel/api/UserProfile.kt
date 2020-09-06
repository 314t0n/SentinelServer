package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

open class UserProfile @JsonCreator constructor(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("created") val created: OffsetDateTime,
        @param:JsonProperty("email") val email: String
)