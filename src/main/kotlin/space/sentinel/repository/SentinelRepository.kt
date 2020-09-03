package space.sentinel.repository

import com.typesafe.config.Config
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory

abstract class SentinelRepository(config: Config) {

    private val mariadbConnectionConfiguration = MariadbConnectionConfiguration.builder()
            .host(config.getString("mariadb.host"))
            .port(config.getInt("mariadb.port"))
            .username(config.getString("mariadb.username"))
            .password(config.getString("mariadb.password"))
            .database(config.getString("mariadb.database"))
            .build();

    val connectionFactory = MariadbConnectionFactory(mariadbConnectionConfiguration)
}