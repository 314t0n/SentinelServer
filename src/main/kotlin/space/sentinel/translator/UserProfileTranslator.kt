package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import io.r2dbc.spi.Row
import space.sentinel.api.*

class UserProfileTranslator @Inject constructor(private val mapper: ObjectMapper,
                                                private val dateTimeTranslator: DateTimeTranslator) : Translator(mapper) {

    fun translate(row: Row) = UserProfile(
            id = row.get("id", String::class.java),
            created = dateTimeTranslator.toDateTime(row),
            email = row.get("email", String::class.java),
            role = UserRole.USER
    )

    fun password(row: Row): String = row.get("password", String::class.java)

    fun toJson(n: UserProfile): String = mapper.writeValueAsString(n)
}