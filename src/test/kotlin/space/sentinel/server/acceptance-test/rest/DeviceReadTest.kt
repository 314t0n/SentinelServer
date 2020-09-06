package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.DeviceController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class DeviceReadTest : AcceptanceTest() {

    @Test
    fun `GET all should return first x element`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).get()

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->
                    assertThat(json, isJson())
                    assertThat(json, hasJsonPath("$.devices", hasSize(5)))
                    assertThat(json, hasJsonPath("$.devices[0].name", equalTo("TEST_DEVICE9")))
                    assertThat(json, hasJsonPath("$.devices[0].api_key", equalTo("test123")))
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `GET by id should return entity`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .withQuery("/2")
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).get()

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->
                    assertThat(json, isJson())
                    assertThat(json, hasJsonPath("$.id", equalTo("2")))
                    assertThat(json, hasJsonPath("$.api_key", equalTo("test1")))
                    assertThat(json, hasJsonPath("$.name", equalTo("TEST_DEVICE2")))
                    true
                }
                .expectComplete()
                .verify()
    }

    @Test
    fun `GET by wrong id should return NOT FOUND`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .withQuery("/invalid")
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).statusCode()

        StepVerifier
                .create(response)
                .expectNext(404)
                .verifyComplete()
    }

    @Test
    fun `GET should paginate`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .withQuery("?page=2")
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).get()

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->

                    assertThat(json, isJson())
                    assertThat(json, hasJsonPath("$.devices", hasSize(5)))
                    assertThat(json, hasJsonPath("$.devices[0].name", equalTo("TEST_DEVICE5")))

                    true
                }
                .expectComplete()
                .verify()
    }
}