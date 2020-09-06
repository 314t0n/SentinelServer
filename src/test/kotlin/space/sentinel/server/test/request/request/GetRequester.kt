package space.sentinel.server.test.request.request

import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

class GetRequester(builder: RequestBuilder) {
    private val client: HttpClient.RequestSender = builder.build() as HttpClient.RequestSender

    fun get(): Mono<String> {
        return client
                .responseContent()
                .retain().aggregate()
                .asString()
    }

    fun statusCode(): Mono<Int> {
        return client
                .response()
                .map { it.status().code() }
    }
}