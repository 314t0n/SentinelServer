package space.sentinel.service

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import space.sentinel.api.EntityId
import space.sentinel.api.Notification
import space.sentinel.api.UserProfile
import space.sentinel.api.request.NotificationRequest
import space.sentinel.repository.UserRepository
import space.sentinel.translator.UserProfileTranslator
import java.lang.IllegalArgumentException

class UserService @Inject constructor(
        private val paginationService: PaginationService,
        private val userRepository: UserRepository,
        private val userProfileTranslator: UserProfileTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

//    fun getAll(params: Map<String, String>): Flux<Notification> {
//        val (limit, offset) = paginationService.pagination(params)
//
//        return userRepository
//                .getAll(limit, offset)
//                .map { userProfileTranslator.translate(it) }
//    }

    fun save(notificationRequest: NotificationRequest): Mono<EntityId> {
        return userRepository
                .save(notificationRequest).map { EntityId(it) }
    }

    fun get(id: String): Mono<UserProfile> {
        return Mono.just(id).flatMap {
            userRepository
                    .get(id.toLong())
                    .map { row -> userProfileTranslator.translate(row) }
                    .switchIfEmpty(Mono.error(IllegalArgumentException()))
                    .doOnError { logger.error(it.message, it) }
        }
    }

    fun userBySessionId(sessionId: String): Mono<UserProfile> {
        return userRepository
                .findBySessionId(sessionId).map { row -> userProfileTranslator.translate(row) }
                .switchIfEmpty(Mono.error(IllegalArgumentException()))
                .doOnError { logger.error(it.message, it) }
    }

}