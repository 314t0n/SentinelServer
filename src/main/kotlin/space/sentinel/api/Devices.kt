package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

open class Devices @JsonCreator constructor(
        @param:JsonProperty("devices") val devices: Iterable<Device>)