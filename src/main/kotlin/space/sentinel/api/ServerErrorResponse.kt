package space.sentinel.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.handler.codec.http.HttpResponseStatus

open class ServerErrorResponse @JsonCreator constructor(
        @param:JsonProperty("errorCode") val errorCode: Int,
        @param:JsonProperty("reason") val reason: String) {

    companion object {
        fun createBadRequest(t: Throwable): ServerErrorResponse = ServerErrorResponse(HttpResponseStatus.BAD_REQUEST.code(), t.message.orEmpty())
        fun createInternalServerError(t: Throwable): ServerErrorResponse = ServerErrorResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), t.message.orEmpty())
    }

}