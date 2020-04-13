package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.ANotificationRequest
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.ANotificationResponse

class SerializationTest() : AcceptanceTest() {

    @Test
    fun `NotificationRequest serialization`() {
        val serialized = objectmapper.writeValueAsString(ANotificationRequest)

        val deserialized = objectmapper.readValue<NotificationRequest>(serialized)

        StepVerifier
                .create(Mono.just(deserialized))
                .expectNext(ANotificationRequest)
    }

    @Test
    fun `NotificationResponse serialization`() {
        val serialized = objectmapper.writeValueAsString(ANotificationResponse)

        val deserialized = objectmapper.readValue<NotificationResponse>(serialized)

        StepVerifier
                .create(Mono.just(deserialized))
                .expectNext(ANotificationResponse)
    }

}