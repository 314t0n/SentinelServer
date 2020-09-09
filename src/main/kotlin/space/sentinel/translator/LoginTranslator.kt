package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import space.sentinel.api.*
import space.sentinel.api.request.LoginRequest

class LoginTranslator @Inject constructor(private val mapper: ObjectMapper,
                                          private val dateTimeTranslator: DateTimeTranslator) : Translator(mapper) {
    fun translateRequest(string: String): LoginRequest {
        return mapper.readValue(string, LoginRequest::class.java)
    }

    fun translate(userSession: UserSession): String {
        return mapper.writeValueAsString(userSession)
    }
}