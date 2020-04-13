package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class ServerErrorResponse @JsonCreator constructor(
        @param:JsonProperty("errorCode") val errorCode: Int,
        @param:JsonProperty("reason") val reason: String)