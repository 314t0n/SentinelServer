package space.sentinel.util

import reactor.netty.http.server.HttpServerRequest
import java.util.stream.Collectors

class QueryParameterResolver {

    fun parameterMap(request: HttpServerRequest): Map<String, String> {
        val query = request.uri().split("?")

        val parameters = query.stream()
                .skip(1)
                .map { it.split("&") }

        val keyValues = parameters.flatMap { it.stream() }
                .map { it.split("=") }
                .collect(Collectors.toList())

        return keyValues.associateBy({ it[0] }, { it[1] })
    }

}