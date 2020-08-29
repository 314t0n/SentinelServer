package space.sentinel.api.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR

open class ServerErrorResponse @JsonCreator constructor(
        @param:JsonProperty("errorCode") val errorCode: Int,
        @param:JsonProperty("reason") val reason: String) {

    companion object {
        fun createBadRequest(t: Throwable): ServerErrorResponse = ServerErrorResponse(BAD_REQUEST.code(), t.message.orEmpty())
        fun createInternalServerError(t: Throwable): ServerErrorResponse = ServerErrorResponse(INTERNAL_SERVER_ERROR.code(), t.message.orEmpty())
    }

}