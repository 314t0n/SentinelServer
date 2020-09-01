package space.sentinel.service

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import space.sentinel.api.Notification
import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.response.NotificationResponse
import space.sentinel.repository.FileImageRepository
import space.sentinel.repository.InMemoryCache
import space.sentinel.repository.NotificationRepository
import space.sentinel.repository.entity.NotificationEntity
import space.sentinel.translator.NotificationEntityTranslator
import space.sentinel.translator.NotificationTranslator
import java.util.*

class NotificationService @Inject constructor(private val fileImageRepository: FileImageRepository,
                                              private val inMemoryCache: InMemoryCache,
                                              private val paginationService: PaginationService,
                                              private val notificationRepository: NotificationRepository,
                                              private val notificationTranslator: NotificationTranslator,
                                              private val notificationEntityTranslator: NotificationEntityTranslator) {

    companion object {
        private val NoImageFilenameFallback = Mono.just("no image")

    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(params: Map<String, String>): Flux<Notification> {
        val (limit, offset) = paginationService.pagination(params)

        return notificationRepository
                .getAll(limit, offset)
                .map {  notificationTranslator.translate(it) }
    }

    fun save(notification: Mono<NotificationRequest>): Mono<NotificationResponse> =
            notification
                    .map { request -> notificationEntityTranslator.translateToEntity(request, saveImage(request)) }
                    .map(this::persist)
                    .flatMap(notificationEntityTranslator::translateToResponse)
                    .doOnError { logger.error(it.message, it) }

    private fun persist(notificationEntity: Mono<NotificationEntity>): Mono<String> =
            notificationEntity
                    .flatMap(inMemoryCache::cache)
                    .map(NotificationEntity::filename)

    private fun saveImage(request: NotificationRequest): Mono<String> =
            if (request.image.isPresent) {
                fileImageRepository.save(notificationEntityTranslator.translateToImageEntity(request))
            } else {
                NoImageFilenameFallback
            }
}