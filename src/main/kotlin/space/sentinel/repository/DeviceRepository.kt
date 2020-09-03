package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config
import io.r2dbc.spi.Row
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import space.sentinel.api.Device
import space.sentinel.api.Notification
import space.sentinel.api.request.DeviceRequest
import space.sentinel.translator.NotificationTranslator
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class DeviceRepository @Inject constructor(config: Config) : SentinelRepository(config) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(limit: Long, offset: Long): Flux<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.device x ORDER BY x.created DESC LIMIT ?,?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, limit).bind(1, offset).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
    }

    fun get(id: Long): Mono<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.device x WHERE id=?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, id).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

    fun save(device: DeviceRequest): Mono<Long> {
        val created = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val selectQuery = "INSERT INTO device(created, name, api_key) VALUES (?, ?, ?)"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(selectQuery)
                            .bind(0, created)
                            .bind(1, device.name)
                            .bind(2, device.apiKey)
                            .returnGeneratedValues("id")
                            .execute()
                }
                .flatMap { result -> result.map { r, _ -> r.get("id", String::class.java)!!.toLong() } }
                .doOnError { logger.error(it.message) }
                .toMono()
    }

}