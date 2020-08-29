package space.sentinel.repository

import io.r2dbc.spi.Row
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import reactor.core.publisher.Flux
import space.sentinel.api.NotificationType
import space.sentinel.repository.entity.NotificationEntity
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class NotificationRepository {

    /**
     *  Error finishing response. Closing connection
    io.r2dbc.spi.R2dbcTransientResourceException: No decoder for type java.time.OffsetDateTime and column type TIMESTAMP
     */

    fun getAll(): Flux<NotificationEntity> {
        val conf = MariadbConnectionConfiguration.builder()
                .host("localhost")
                .port(3306)
                .username("root")
                .password("testelek")
                .database("sentinel")
                .build();
        val connectionFactory = MariadbConnectionFactory(conf);

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement("SELECT x.* FROM sentinel.notification x").execute() }
                .flatMap { result ->
                    result.map { row, meta ->
                        translate(row)
                    }
                }
    }

    private fun translate(row: Row) = NotificationEntity(
            id = row.get("id", String::class.java),
            timestamp = toDateTime(row),
            deviceId = row.get("device_id", String::class.java),
            message = row.get("message", String::class.java),
            filename = "test",
            type = NotificationType.INFO,
            image = Optional.empty()
    )

    private fun toDateTime(row: Row): OffsetDateTime {
        val ts = row.get("created", String::class.java)
        val local = LocalDateTime.parse(ts, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return OffsetDateTime.of(local, ZoneOffset.UTC)
    }

}