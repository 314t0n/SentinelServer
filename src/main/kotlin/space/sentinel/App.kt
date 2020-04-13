package space.sentinel

import org.slf4j.LoggerFactory
import space.sentinel.server.SentinelServer
import space.sentinel.server.Wiring

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Sentinel")
    try {
        val wiring = Wiring("dev")

        SentinelServer()
                .create(wiring)
                .onDispose()
                .doOnError { e -> logger.error(e.message, e) }
                .block()
    } catch (ex: Exception) {
        logger.error(ex.message)
    }
}
