package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import io.r2dbc.spi.Row
import reactor.core.publisher.Mono
import space.sentinel.api.*
import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.response.NotificationResponse
import space.sentinel.api.response.ServerErrorResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class UserProfileTranslator @Inject constructor(private val mapper: ObjectMapper,
                                                private val dateTimeTranslator: DateTimeTranslator) : Translator(mapper) {

    fun translate(row: Row) = UserProfile(
            id = row.get("id", String::class.java),
            created = dateTimeTranslator.toDateTime(row),
            email = row.get("email", String::class.java)
    )

    fun toJson(n: UserProfile): String = mapper.writeValueAsString(n)
}