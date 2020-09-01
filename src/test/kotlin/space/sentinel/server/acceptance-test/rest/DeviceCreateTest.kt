package space.sentinel.server.`acceptance-test`.rest

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.DeviceController
import space.sentinel.server.`acceptance-test`.AcceptanceTest
import space.sentinel.server.`acceptance-test`.DomainObjects

class DeviceCreateTest : AcceptanceTest() {

    @Test
    fun `POST should return empty body`() {
        val requestString = mapper.writeValueAsString(DomainObjects.DeviceRequest)

        val response = post(requestString, DeviceController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectComplete()
                .verify()
    }

    @Test
    fun `POST should respond CREATED`() {
        val requestString = mapper.writeValueAsString(DomainObjects.DeviceRequest)

        val response = statusCode(requestString, DeviceController.CONTROLLER_PATH)

        StepVerifier
                .create(response)
                .expectNext(201)
                .verifyComplete()
    }

}