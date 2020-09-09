package space.sentinel.controller

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.netty.handler.codec.http.cookie.DefaultCookie
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes
import space.sentinel.api.UserSession
import space.sentinel.api.response.ServerErrorResponse.Companion.createErrorResponse
import space.sentinel.exception.UnauthorizedException
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.service.ApiKeyService
import space.sentinel.service.AuthService
import space.sentinel.service.UserService
import space.sentinel.translator.LoginTranslator
import space.sentinel.util.QueryParameterResolver

class AuthenticationController @Inject constructor(private val authService: AuthService,
                                                   private val translator: LoginTranslator,
                                                   userService: UserService,
                                                   apiKeyService: ApiKeyService) : SentinelController(apiKeyService, userService) {

    companion object {
        const val CONTROLLER_LOGIN = "auth/login"
        const val CONTROLLER_LOGOUT = "auth/logout"
        const val SESSION_ID = "session_id"
    }

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun register(routes: HttpServerRoutes) {
        routes
                .post("/$CONTROLLER_LOGIN") { request, response -> login(request, response) }
                .post("/$CONTROLLER_LOGOUT") { request, response -> logout(request, response) }
    }

    private fun login(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        return request
                .receive()
                .aggregate()
                .asString()
                .map(translator::translateRequest)
                .map(authService::login)
                .flatMap {
                    it.map(this@AuthenticationController::createSessionCookie)
                            .flatMap { cookie ->
                                response
                                        .status(FOUND)
                                        .addCookie(cookie)
                                        .send()
                                        .then()
                            }
                }
                .onErrorResume(JsonParseException::class.java) {
                    logger.warn(it.message, it)
                    badRequest(response, translator.translateError(createErrorResponse(it)))
                }
                .onErrorResume(UnauthorizedException::class.java) {
                    logger.warn(it.message, it)
                    unauthorized(response)
                }
                .onErrorResume(Exception::class.java) {
                    logger.error(it.message, it)
                    internalServerError(response, translator.translateError(createErrorResponse(it)))
                }
                .then()
    }

    private fun logout(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        return withUserProfile(request)
                .flatMap { withSessionId(request) }
                .map(authService::logout)
                .flatMap {
                    response
                            .status(OK)
                            .send()
                            .then()
                }
                .onErrorResume(UnauthorizedException::class.java) {
                    logger.warn(it.message, it)
                    unauthorized(response)
                }
                .onErrorResume(Exception::class.java) {
                    logger.error(it.message, it)
                    internalServerError(response, translator.translateError(createErrorResponse(it)))
                }
                .then()
    }

    private fun createSessionCookie(userSession: UserSession): DefaultCookie {
        val cookie = DefaultCookie(SESSION_ID, userSession.id)
        cookie.setMaxAge(userSession.maxAge)
        cookie.isSecure = true
        return cookie
    }

}