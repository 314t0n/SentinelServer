package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class Notifications @JsonCreator constructor(
        @param:JsonProperty("notifications") val notifications: Iterable<Notification>)