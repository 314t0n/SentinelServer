package space.sentinel.server.`acceptance-test`.rest


import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath
import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import space.sentinel.controller.NotificationController
import space.sentinel.controller.NotificationController.Companion.CONTROLLER_PATH
import space.sentinel.controller.SentinelController.Companion.API_KEY_HEADER
import space.sentinel.server.`acceptance-test`.AcceptanceTest

class NotificationReadTest : AcceptanceTest() {

    @Test
    fun `GET should return first x element`() {
        val response = get()

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

    private fun get(requestUri: String = CONTROLLER_PATH, query: String = ""): Mono<String> {
        return client
                .headers { h -> h.set(API_KEY_HEADER, "test").set("Content-type", "application/json") }
                .get()
                .uri("""${serverUrl(requestUri)}$query""")
                .responseContent()
                .retain().aggregate()
                .asString()
    }

}