package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import space.sentinel.api.request.NotificationRequest
import space.sentinel.server.`acceptance-test`.DomainObjects.Companion.AlertNotificationWithImage

class SerializationTest() : AcceptanceTest() {

    @Test
    fun `NotificationRequest serialization`() {
        val serialized = mapper.writeValueAsString(AlertNotificationWithImage)

        val deserialized = mapper.readValue<NotificationRequest>(serialized)

        StepVerifier
                .create(Mono.just(deserialized))
                .expectNext(AlertNotificationWithImage)
    }

}