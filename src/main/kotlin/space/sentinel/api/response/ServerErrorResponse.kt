package space.sentinel.api.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR

open class ServerErrorResponse @JsonCreator constructor(
        @param:JsonProperty("reason") val reason: String) {

    companion object {
        fun createErrorResponse(t: Throwable): ServerErrorResponse = ServerErrorResponse(t.message.orEmpty())
    }

}