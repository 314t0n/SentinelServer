package space.sentinel.service

import com.google.inject.Inject
import io.r2dbc.spi.Row
import org.mindrot.jbcrypt.BCrypt
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.util.function.Tuple2
import space.sentinel.api.Device
import space.sentinel.api.UserProfile
import space.sentinel.api.UserSession
import space.sentinel.api.request.LoginRequest
import space.sentinel.controller.SentinelController
import space.sentinel.exception.UnauthorizedException
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.repository.UserRepository
import space.sentinel.translator.DeviceTranslator
import space.sentinel.translator.UserProfileTranslator
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class ApiKeyService @Inject constructor(
        private val apiKeyRepository: ApiKeyRepository,
        private val deviceTranslator: DeviceTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * If apikey is in present and found in the database return device data
     *
     * @param request
     * @param response
     * @param requestHandler callback function in case of valid api key
     */
    fun deviceByApiKey(apiKey: Optional<String>): Mono<Device> {
        return apiKey
                .map { apiKeyRepository.findApiKey(it) }
                .map { row ->
                    row.map { deviceTranslator.translate(it) }
                            .switchIfEmpty(Mono.error(UnauthorizedException("Apikey not found in Database: ${apiKey.get()}")))
                }
                .orElse(Mono.error(UnauthorizedException("Apikey not provided")))
    }
}