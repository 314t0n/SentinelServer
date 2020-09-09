package space.sentinel.server.test.request.request

import io.netty.handler.codec.http.cookie.DefaultCookie
import reactor.netty.http.client.HttpClient
import space.sentinel.controller.AuthenticationController.Companion.SESSION_ID
import space.sentinel.controller.SentinelController

sealed class RequestBuilder(private val baseUri: String) {
    protected var client: HttpClient = HttpClient.create()
    protected var uri: String = ""
    protected var query: String = ""

    fun withApiKey(apiKey: String = "test1"): RequestBuilder {
        client = client.headers { h -> h.set(SentinelController.API_KEY_HEADER, apiKey) }
        return this
    }

    fun withAuth(sessionId: String = "eyboss"): RequestBuilder {
        client = client.cookie(DefaultCookie(SESSION_ID, sessionId))
        return this
    }

    fun uri(path: String): RequestBuilder {
        uri = path
        return this
    }

    fun withQuery(queryString: String): RequestBuilder {
        query = queryString
        return this
    }

    protected fun serverUrl(path: String): String {
        return "$baseUri/$path"
    }

    abstract fun build(): HttpClient.ResponseReceiver<*>
}

class GetRequestBuilder(baseUri: String) : RequestBuilder(baseUri) {

    override fun build(): HttpClient.ResponseReceiver<*> {
        return client.get()
                .uri("""${serverUrl(uri)}$query""")
    }
}

class PostRequestBuilder(baseUri: String) : RequestBuilder(baseUri) {

    override fun build(): HttpClient.RequestSender {
        return client.post()
                .uri(serverUrl(this.uri))
    }
}