package space.sentinel.translator

import io.r2dbc.spi.Row
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DateTimeTranslator {

    fun toDateTime(row: Row): OffsetDateTime {
        val ts = row.get("created", String::class.java)
        val local = LocalDateTime.parse(ts, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return OffsetDateTime.of(local, ZoneOffset.UTC)
    }

}