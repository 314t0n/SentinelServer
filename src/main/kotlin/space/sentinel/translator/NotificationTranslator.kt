package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import reactor.core.publisher.Mono
import space.sentinel.api.Notifications
import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.response.NotificationResponse
import space.sentinel.api.response.NotificationsResponse
import space.sentinel.api.response.ServerErrorResponse

class NotificationTranslator @Inject constructor(private val mapper: ObjectMapper) {

    fun translateRequest(request: String): Mono<NotificationRequest> {
        return Mono.just(mapper.readValue<NotificationRequest>(request))
    }

    fun translateResponse(response: Mono<NotificationResponse>): Mono<String> {
        return response.map(mapper::writeValueAsString)
    }

    fun translateResponse(response: NotificationResponse): String {
        return mapper.writeValueAsString(response)
    }

    fun translate(response: Notifications): String {
        return mapper.writeValueAsString(response)
    }

    fun translateNotificationsResponse(response: NotificationsResponse): String {
        return mapper.writeValueAsString(response)
    }

    fun translateError(error: ServerErrorResponse): Mono<String> {
        return Mono.just(mapper.writeValueAsString(error))
    }

}