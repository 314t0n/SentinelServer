package space.sentinel.server.test.request.request

import io.netty.handler.codec.http.cookie.Cookie
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient

class PostRequester(builder: RequestBuilder) {
    private val client: HttpClient.RequestSender = builder.build() as HttpClient.RequestSender

    fun post(query: String = ""): Mono<String> {
        return client
                .send(ByteBufFlux.fromString(Flux.just(query)))
                .responseContent()
                .retain().aggregate()
                .asString()
    }

    fun statusCode(query: String = ""): Mono<Int> {
        return client
                .send(ByteBufFlux.fromString(Flux.just(query)))
                .response()
                .map { it.status().code() }
    }

    fun cookies(query: String = ""): Mono<MutableMap<CharSequence, MutableSet<Cookie>>> {
        return client
                .send(ByteBufFlux.fromString(Flux.just(query)))
                .response()
                .map { it.cookies() }
    }
}