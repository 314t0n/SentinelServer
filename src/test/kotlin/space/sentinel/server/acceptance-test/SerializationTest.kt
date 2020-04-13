package space.sentinel.server.`acceptance-test`

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import space.sentinel.domain.Notification

class SerializationTest() : AcceptanceTest() {

    @Test
    fun `Notification serialization`() {
        val serialized = objectmapper.writeValueAsString(DomainObjects.AMotionDetectAlert)

        val deserialized = objectmapper.readValue<Notification>(serialized)

        StepVerifier
                .create(Mono.just(deserialized))
                .expectNext(DomainObjects.AMotionDetectAlert)
    }

    @Test
    fun `MotionDetectetAlert serialization`() {
        val serialized = objectmapper.writeValueAsString(DomainObjects.AMotionDetectAlert)

        val deserialized = objectmapper.readValue<Notification>(serialized)

        StepVerifier
                .create(Mono.just(deserialized))
                .expectNext(DomainObjects.AMotionDetectAlert)
    }

}