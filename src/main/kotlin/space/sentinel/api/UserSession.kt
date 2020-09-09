package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class UserSession @JsonCreator constructor(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("max-age") val maxAge: Long
)