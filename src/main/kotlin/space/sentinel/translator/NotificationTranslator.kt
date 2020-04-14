package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import reactor.core.publisher.Mono
import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import space.sentinel.api.ServerErrorResponse

class NotificationTranslator @Inject constructor(private val objectmapper: ObjectMapper) {

    fun translateRequest(request: String): Mono<NotificationRequest> {
        return Mono.just(objectmapper.readValue<NotificationRequest>(request))
    }

    fun translateResponse(response: Mono<NotificationResponse>): Mono<String> {
        return response.map { objectmapper.writeValueAsString(it) }
    }

    fun translateError(error: ServerErrorResponse): Mono<String> {
        return Mono.just(objectmapper.writeValueAsString(error))
    }

}