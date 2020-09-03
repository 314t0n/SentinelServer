package space.sentinel.server.`acceptance-test`.rest

import com.jayway.jsonpath.matchers.JsonPathMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.api.EntityId
import space.sentinel.api.request.DeviceRequest
import space.sentinel.controller.DeviceController
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class DeviceCreateTest : AcceptanceTest() {

    @Test
    fun `POST should respond CREATED`() {
        val requestString = mapper.writeValueAsString(DomainObjects.DeviceRequest)

        val response = statusCode(requestString, DeviceController.CONTROLLER_PATH)

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
        val postResponse = post(mapper.writeValueAsString(deviceRequest), DeviceController.CONTROLLER_PATH).block()
        val entityId = mapper.readValue(postResponse, EntityId::class.java)

        val response = get(DeviceController.CONTROLLER_PATH, "/${entityId.id}")

        StepVerifier
                .create(response)
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