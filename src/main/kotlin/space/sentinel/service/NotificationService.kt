package space.sentinel.service

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import space.sentinel.repository.FileImageRepository
import space.sentinel.repository.InMemoryRepository
import space.sentinel.repository.entity.NotificationEntity
import space.sentinel.translator.NotificationEntityTranslator

class NotificationService @Inject constructor(private val fileImageRepository: FileImageRepository,
                                              private val inMemoryRepository: InMemoryRepository,
                                              private val notificationEntityTranslator: NotificationEntityTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun save(notification: Mono<NotificationRequest>): Mono<NotificationResponse> {
        return notification
                .map { request -> notificationEntityTranslator.translateToEntity(request, saveImage(request)) }
                .map(this::persist)
                .flatMap(notificationEntityTranslator::translateToResponse)
                .doOnError { logger.error(it.message, it) }
    }

    private fun persist(notificationEntity: Mono<NotificationEntity>): Mono<String> =
            notificationEntity
                    .flatMap(inMemoryRepository::save)
                    .map(NotificationEntity::filename)


    private fun saveImage(request: NotificationRequest): Mono<String> =
            if (request.image.isPresent) {
                fileImageRepository.save(notificationEntityTranslator.translateToImageEntity(request))
            } else {
                Mono.empty()
            }
}