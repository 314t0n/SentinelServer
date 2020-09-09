package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config
import io.r2dbc.spi.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import space.sentinel.api.UserProfile
import space.sentinel.api.request.DeviceRequest
import java.time.OffsetDateTime

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

    fun save(device: DeviceRequest, userProfile: UserProfile): Mono<Long> {
        val created = timestampFormat(OffsetDateTime.now())
        val query = "INSERT INTO device(created, name, api_key, user_id) VALUES (?, ?, ?, ?)"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(query)
                            .bind(0, created)
                            .bind(1, device.name)
                            .bind(2, device.apiKey)
                            .bind(3, userProfile.id)
                            .returnGeneratedValues("id")
                            .execute()
                }
                .flatMap { result ->
                    result.map { r, _ -> r.get("id", String::class.java)!!.toLong() } }
                .doOnError { logger.error(it.message) }
                .toMono()
    }

}