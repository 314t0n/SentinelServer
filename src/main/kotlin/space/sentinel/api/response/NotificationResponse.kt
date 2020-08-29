package space.sentinel.api.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

open class NotificationResponse @JsonCreator constructor(
        @param:JsonProperty("modified") val modified: OffsetDateTime,
        @param:JsonProperty("databaseId") val databaseId: String)