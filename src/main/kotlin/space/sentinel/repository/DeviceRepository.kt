package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config
import io.r2dbc.spi.Row
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import space.sentinel.api.Device
import space.sentinel.api.Notification
import space.sentinel.translator.NotificationTranslator


class DeviceRepository @Inject constructor(config: Config) {

    private val mariadbConnectionConfiguration = MariadbConnectionConfiguration.builder()
            .host(config.getString("mariadb.host"))
            .port(config.getInt("mariadb.port"))
            .username(config.getString("mariadb.username"))
            .password(config.getString("mariadb.password"))
            .database(config.getString("mariadb.database"))
            .build();

    private val connectionFactory = MariadbConnectionFactory(mariadbConnectionConfiguration)

    fun getAll(limit: Long, offset: Long): Flux<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.device x ORDER BY x.created DESC LIMIT $limit,$offset" // bind not supported

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).execute() }
                .flatMap { result ->
                    result.map { row, meta -> row }
                }
    }

    fun get(id: Long): Mono<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.device x WHERE id=$id" // bind not supported

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).execute() }
                .flatMap { result ->
                    result.map { row, meta -> row }
                }.toMono()
    }

}