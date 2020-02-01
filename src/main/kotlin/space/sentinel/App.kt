package space.sentinel

import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Sentinel")
    try {
        SentinelApplication()
                .start("dev")
                .onDispose()
                .doOnError { e -> logger.error(e.message, e) }
                .block()
    } catch (ex: Exception) {
        logger.error(ex.message)
    }
}
