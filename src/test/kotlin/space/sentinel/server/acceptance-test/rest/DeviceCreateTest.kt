package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonpath.matchers.JsonPathMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.api.EntityId
import space.sentinel.api.request.DeviceRequest
import space.sentinel.controller.DeviceController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects
import space.sentinel.server.test.request.request.GetRequestBuilder
import space.sentinel.server.test.request.request.GetRequester
import space.sentinel.server.test.request.request.PostRequestBuilder
import space.sentinel.server.test.request.request.PostRequester

class DeviceCreateTest : AcceptanceTest() {

    @Test
    fun `POST should respond CREATED`() {
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .uri(CONTROLLER_PATH)

        val response = PostRequester(request)
                .statusCode(mapper.writeValueAsString(DomainObjects.DeviceRequest))

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

    @Test
    fun `POST should create new entity`() {
        val deviceRequest = DeviceRequest(
                apiKey = "asd-bge",
                name = "doom666"
        )
        val request = PostRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .uri(CONTROLLER_PATH)
        val postResponse = PostRequester(request).post(mapper.writeValueAsString(deviceRequest)).block()
        val entityId = mapper.readValue(postResponse, EntityId::class.java)

        val getRequest = GetRequestBuilder(baseUri)
                .withApiKey()
                .withAuth()
                .withQuery("/${entityId.id}")
                .uri(CONTROLLER_PATH)
        val getResponse = GetRequester(getRequest).get()

        StepVerifier
                .create(getResponse)
                .expectNextMatches { json: String ->
                    assertThat(json, JsonPathMatchers.isJson())
                    assertThat(json, JsonPathMatchers.hasJsonPath("$.id", equalTo(entityId.id.toString())))
                    assertThat(json, JsonPathMatchers.hasJsonPath("$.api_key", equalTo(deviceRequest.apiKey)))
                    assertThat(json, JsonPathMatchers.hasJsonPath("$.name", equalTo(deviceRequest.name)))
                    true
                }
                .expectComplete()
                .verify()
    }

}