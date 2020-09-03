package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import reactor.core.publisher.Mono
import space.sentinel.api.EntityId
import space.sentinel.api.response.ServerErrorResponse

open class Translator @Inject constructor(private val mapper: ObjectMapper) {

    fun translateError(error: ServerErrorResponse): Mono<String> {
        return Mono.just(mapper.writeValueAsString(error))
    }

    fun translateId(id: EntityId): String {
        return  mapper.writeValueAsString(id)
    }

}