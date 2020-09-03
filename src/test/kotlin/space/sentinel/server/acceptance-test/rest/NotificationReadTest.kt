package space.sentinel.server.`acceptance-test`.rest


import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.server.`acceptance-test`.AcceptanceTest

class NotificationReadTest : AcceptanceTest() {

    @Test
    fun `GET by id should return entity`() {
        val response = get("$CONTROLLER_PATH", "/2")

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
        val response = statusCode("$CONTROLLER_PATH/invalid")

        StepVerifier
                .create(response)
                .expectNext(404)
                .verifyComplete()
    }

    @Test
    fun `GET should return first x element`() {
        val response = get("$CONTROLLER_PATH")

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
        val response = get("$CONTROLLER_PATH", "?page=2")

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