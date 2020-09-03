package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class EntityId @JsonCreator constructor(
        @param:JsonProperty("id") val id: Long)