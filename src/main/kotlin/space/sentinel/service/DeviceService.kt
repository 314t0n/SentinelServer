package space.sentinel.service

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import space.sentinel.api.Device
import space.sentinel.api.request.DeviceRequest
import space.sentinel.repository.DeviceRepository
import space.sentinel.translator.DeviceTranslator
import java.lang.IllegalArgumentException

class DeviceService @Inject constructor(private val paginationService: PaginationService,
                                        private val deviceRepository: DeviceRepository,
                                        private val deviceTranslator: DeviceTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getAll(params: Map<String, String>): Flux<Device> {
        val (limit, offset) = paginationService.pagination(params)

        return deviceRepository
                .getAll(limit, offset)
                .map { row -> deviceTranslator.translate(row) }
                .doOnError { logger.error(it.message, it) }
    }

    fun get(id: String): Mono<Device> {
        return Mono.just(id).flatMap {
            deviceRepository
                    .get(id.toLong())
                    .map { row -> deviceTranslator.translate(row) }
                    .switchIfEmpty(Mono.error(IllegalArgumentException()))
                    .doOnError { logger.error(it.message, it) }
        }
    }

    fun save(device: DeviceRequest): Mono<Void> {
        return Mono.just("").then()
    }

}