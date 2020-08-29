package space.sentinel.api.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

open class NotificationsResponse @JsonCreator constructor(
        @param:JsonProperty("notifications") val notifications: Iterable<NotificationResponse>)