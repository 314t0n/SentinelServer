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

class ApiKeyRepository @Inject constructor(config: Config) : SentinelRepository(config) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun findApiKey(apiKey: String): Mono<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.device x WHERE api_key=?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, apiKey).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

}