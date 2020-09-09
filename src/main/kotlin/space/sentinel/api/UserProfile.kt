package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

enum class UserRole{
    ADMIN, USER
}

open class UserProfile @JsonCreator constructor(
        @param:JsonProperty("id") val id: String,
        @param:JsonProperty("created") val created: OffsetDateTime,
        @param:JsonProperty("role") val role: UserRole,
        @param:JsonProperty("email") val email: String
)