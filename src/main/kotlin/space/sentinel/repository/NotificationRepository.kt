package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import reactor.core.publisher.Flux
import space.sentinel.api.Notification
import space.sentinel.translator.NotificationTranslator


class NotificationRepository @Inject constructor(
        config: Config,
        private val notificationTranslator: NotificationTranslator) {

    private val mariadbConnectionConfiguration = MariadbConnectionConfiguration.builder()
            .host(config.getString("mariadb.host"))
            .port(config.getInt("mariadb.port"))
            .username(config.getString("mariadb.username"))
            .password(config.getString("mariadb.password"))
            .database(config.getString("mariadb.database"))
            .build();

    private val connectionFactory = MariadbConnectionFactory(mariadbConnectionConfiguration)

    fun getAll(limit: Long, offset: Long): Flux<Notification> {
        val selectQuery = "SELECT x.* FROM sentinel.notification x ORDER BY x.created DESC LIMIT $limit,$offset" // bind not supported

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).execute() }
                .flatMap { result ->
                    result.map { row, meta ->
                        notificationTranslator.translate(row)
                    }
                }
    }

}