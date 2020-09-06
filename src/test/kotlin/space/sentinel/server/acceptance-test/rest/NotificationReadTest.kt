package space.sentinel.server.`acceptance-test`.rest


import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.DeviceController
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class NotificationReadTest : AcceptanceTest() {

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
                    assertThat(json, hasJsonPath("$.message", equalTo("test message2")))
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
    fun `GET should return first x element`() {
        val request = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .uri(CONTROLLER_PATH)

        val response = GetRequester(request).get()

        StepVerifier
                .create(response)
                .expectNextMatches { json: String ->
                    assertThat(json, isJson())
                    assertThat(json, hasJsonPath("$.notifications", hasSize(5)))
                    assertThat(json, hasJsonPath("$.notifications[0].id", equalTo("10")))
                    assertThat(json, hasJsonPath("$.notifications[0].message", equalTo("test message10")))
                    assertThat(json, hasJsonPath("$.notifications[0].device_id", equalTo("1")))
                    true
                }
                .expectComplete()
                .verify()
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
                    assertThat(json, hasJsonPath("$.notifications", hasSize(5)))
                    assertThat(json, hasJsonPath("$.notifications[0].message", equalTo("test message5")))
                    true
                }
                .expectComplete()
                .verify()
    }

}