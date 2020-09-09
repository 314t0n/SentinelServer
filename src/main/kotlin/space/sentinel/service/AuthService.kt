package space.sentinel.service

import com.google.inject.Inject
import io.r2dbc.spi.Row
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import space.sentinel.api.UserProfile
import space.sentinel.api.UserSession
import space.sentinel.api.request.LoginRequest
import space.sentinel.exception.UnauthorizedException
import space.sentinel.repository.UserRepository
import space.sentinel.translator.UserProfileTranslator
import java.security.MessageDigest
import java.security.SecureRandom

class AuthService @Inject constructor(
        private val userRepository: UserRepository,
        private val userProfileTranslator: UserProfileTranslator) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Finds user by email and compare the encrypted password from the db to the provided one
     *
     * @param loginRequest email/pass for user
     * @exception UnauthorizedException if user not found by email or passwords not matching
     */
    fun login(loginRequest: LoginRequest): Mono<UserSession> {
        val email = loginRequest.email
        val password = loginRequest.decodedPassword()

        val findByCredentials = userRepository
                .findByEmail(email)
                .doOnError { logger.error(it.message, it) }

        return findByCredentials
                .filter { isValidPassword(it, password) }
                .map {
                    val userProfile = userProfileTranslator.translate(it)
                    val sessionId = generateSessionId()
                    Pair(userProfile, sessionId)
                }
                .map { (userProfile, sessionId) ->
                    userRepository.saveSessionId(userProfile, sessionId)
                }
                .flatMap { it }
                .doOnError { logger.error(it.message, it) }
                .switchIfEmpty(Mono.error(UnauthorizedException("Email: $email")))
    }

    /**
     * Removes session_id from the database
     *
     * @param sessionId valid session id
     * @exception UnauthorizedException if session id not found in database, although session id must be validated before calling this method
     */
    fun logout(sessionId: String): Mono<Int> {
        return userRepository.removeSession(sessionId)
                .doOnEach{ logger.info(it.toString())}
                .filter { it > 0 }
                .switchIfEmpty(Mono.error(UnauthorizedException("SessionId not found: $sessionId")))
                .doOnError { logger.error(it.message, it) }
    }

    private fun isValidPassword(row: Row, passwordFromUser: String): Boolean {
        val passwordFromDB = userProfileTranslator.password(row)
        return BCrypt.checkpw(passwordFromUser, passwordFromDB)
    }

    private fun generateSessionId(): String {
        val md = MessageDigest.getInstance("SHA3-256")
        val random = SecureRandom()
        val randomBytes = ByteArray(16)
        random.nextBytes(randomBytes)

        val hash: ByteArray = md.digest(randomBytes)
        return bytesToHex(hash)
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

}