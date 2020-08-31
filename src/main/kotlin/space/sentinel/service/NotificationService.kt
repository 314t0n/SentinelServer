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
import java.util.*

class NotificationService @Inject constructor(private val fileImageRepository: FileImageRepository,
                                              private val inMemoryCache: InMemoryCache,
                                              private val notificationRepository: NotificationRepository,
                                              private val notificationEntityTranslator: NotificationEntityTranslator) {

    companion object {
        private val NoImageFilenameFallback = Mono.just("no image")
        const val DEFAULT_PAGE_SIZE = 5L
        const val FIRST_PAGE = 0L
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(params: Map<String, String>): Flux<Notification> {
        val requestedPageNumber = Optional.ofNullable(params["page"])
                .map(String::toLong)
                .map { number -> number.minus(1) }
                .orElse(FIRST_PAGE)

        val limit: Long = requestedPageNumber * DEFAULT_PAGE_SIZE
        val offset: Long = DEFAULT_PAGE_SIZE

        return notificationRepository.getAll(limit, offset)
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