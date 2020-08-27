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
        println("Request translation: $request")
        return Mono.just(objectmapper.readValue<NotificationRequest>(request))
    }

    fun translateResponse(response: Mono<NotificationResponse>): Mono<String> {
        return response
                .map { val writeValueAsString = objectmapper.writeValueAsString(it)
                    println("Response without: $it")
                    println("Response translation1: $writeValueAsString")
                    writeValueAsString
                }
                .doOnEach { println("Response translation2: ${it.get()}") }
                .doOnError { println("Response error: ${it.message}") }
    }

    fun translateError(error: ServerErrorResponse): Mono<String> {
        println("Translate error: ${error.reason}")
        println("Translate error: ${error.errorCode}")
        return Mono.just(objectmapper.writeValueAsString(error))
    }

}