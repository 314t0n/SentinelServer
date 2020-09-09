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
import space.sentinel.api.UserProfile
import space.sentinel.api.UserSession
import space.sentinel.api.request.NotificationRequest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class UserRepository @Inject constructor(
        config: Config) : SentinelRepository(config) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun get(id: Long): Mono<Row> {
        val selectQuery = "SELECT x.* FROM sentinel.user x WHERE id=?"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, id).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

    fun findBySessionId(sessionId: String): Mono<Row> {
        val selectQuery = "SELECT u.id, u.email, u.created from sentinel.session s LEFT JOIN user u on s.user_id = u.id WHERE session_id=? AND u.active=true"

        return Flux.from(connectionFactory.create())
                .flatMap { c -> c.createStatement(selectQuery).bind(0, sessionId).execute() }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

    fun findByEmail(email: String): Mono<Row> {
        val selectQuery = "SELECT u.* from sentinel.user u WHERE email=? AND active=true"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(selectQuery)
                            .bind(0, email)
                            .execute()
                }
                .flatMap { result -> result.map { row, _ -> row } }
                .toMono()
    }

    fun saveSessionId(userProfile: UserProfile, sessionId: String): Mono<UserSession> {
        val current = OffsetDateTime.now()
        val exp = current.plusDays(1)
        val maxAgeInSeconds = exp.toEpochSecond() - current.toEpochSecond()

        val created = timestampFormat(current)
        val expire = timestampFormat(exp)
        val query = "INSERT INTO session(session_id, created, expired_at, user_id) VALUES (?, ?, ?, ?)"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(query)
                            .bind(0, sessionId)
                            .bind(1, created)
                            .bind(2, expire)
                            .bind(3, userProfile.id)
                            .returnGeneratedValues("id")
                            .execute()
                }
                .flatMap { result ->
                    result.map { r, _ -> UserSession(id = sessionId, maxAge = maxAgeInSeconds) }
                }
                .doOnError { logger.error(it.message) }
                .toMono()
    }

    fun removeSession(sessionId: String): Mono<Int> {
        val query = "DELETE FROM session WHERE session_id=?"

        return Flux.from(connectionFactory.create())
                .flatMap { c ->
                    c.createStatement(query)
                            .bind(0, sessionId)
                            .execute()
                }
                .flatMap { result ->
                    result.rowsUpdated
                }
                .doOnError { logger.error(it.message) }
                .toMono()
    }

}