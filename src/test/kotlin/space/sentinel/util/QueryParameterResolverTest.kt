package space.sentinel.util

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Test
import reactor.netty.http.server.HttpServerRequest

internal class QueryParameterResolverTest {

    private val underTest = QueryParameterResolver()

    @Test
    fun `returns value from query`() {
        val request = mock<HttpServerRequest>()

        whenever(request.uri()).thenReturn("/notification?page=2")

        val result = underTest.parameterMap(request)

        assertThat(result["page"], equalTo("2"))
    }

    @Test
    fun `return empty when param not found`() {
        val request = mock<HttpServerRequest>()

        whenever(request.uri()).thenReturn("/notification?page=2")

        val result = underTest.parameterMap(request)

        assertThat(result.containsKey("batman"), equalTo(false))
    }

    @Test
    fun `returns value when has multiple parameters`() {
        val request = mock<HttpServerRequest>()

        whenever(request.uri()).thenReturn("/notification?page=2&bat=man&pizzatime=toby")

        val result = underTest.parameterMap(request)

        assertThat(result["pizzatime"], equalTo("toby"))
    }
}