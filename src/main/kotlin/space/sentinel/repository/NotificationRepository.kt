package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config
import io.r2dbc.spi.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import space.sentinel.api.request.NotificationRequest
import java.time.OffsetDateTime


class NotificationRepository @Inject constructor(
        config: Config) : SentinelRepository(config) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(limit: Long, offset: Long): Flux<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.notification x ORDER BY x.created DESC LIMIT ?,?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, limit).bind(1, offset).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
    }

    fun save(notificationRequest: NotificationRequest): Mono<Long> {
        val created = timestampFormat(OffsetDateTime.now())
        val selectQuery = "INSERT INTO notification(created, message, device_id, notification_type) VALUES (?, ?, ?, ?)"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(selectQuery)
                            .bind(0, created)
                            .bind(1, notificationRequest.message)
                            .bind(2, notificationRequest.deviceId)
                            .bind(3, notificationRequest.type.id)
                            .returnGeneratedValues("id")
                            .execute()
                }
                .flatMap { result -> result.map { row, _ -> row.get("id", String::class.java)!!.toLong() } }
                .doOnError { logger.error(it.message) }
                .toMono()
    }

    fun get(id: Long, userId: String): Mono<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.notification x WHERE id=?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, id).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

}