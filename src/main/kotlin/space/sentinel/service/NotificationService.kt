package space.sentinel.service

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import space.sentinel.api.EntityId
import space.sentinel.api.Notification
import space.sentinel.api.request.NotificationRequest
import space.sentinel.repository.NotificationRepository
import space.sentinel.translator.NotificationTranslator
import java.lang.IllegalArgumentException

class NotificationService @Inject constructor(
                                              private val paginationService: PaginationService,
                                              private val notificationRepository: NotificationRepository,
                                              private val notificationTranslator: NotificationTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(params: Map<String, String>): Flux<Notification> {
        val (limit, offset) = paginationService.pagination(params)

        return notificationRepository
                .getAll(limit, offset)
                .map {  notificationTranslator.translate(it) }
    }

    fun save(notificationRequest: NotificationRequest): Mono<EntityId> {
        return notificationRepository
                .save(notificationRequest).map { EntityId(it) }
    }

    fun get(id: String): Mono<Notification> {
        return Mono.just(id).flatMap {
            notificationRepository
                    .get(id.toLong())
                    .map { row -> notificationTranslator.translate(row) }
                    .switchIfEmpty(Mono.error(IllegalArgumentException()))
                    .doOnError { logger.error(it.message, it) }
        }
    }

}