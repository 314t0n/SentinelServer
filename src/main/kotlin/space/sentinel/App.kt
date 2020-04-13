package space.sentinel

import com.google.inject.Guice
import com.google.inject.Injector
import org.slf4j.LoggerFactory
import space.sentinel.server.SentinelServer
import space.sentinel.server.modules.SentinelServerModule

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Sentinel")
    try {
        val injector: Injector = Guice.createInjector(SentinelServerModule())

        SentinelServer()
                .create(injector)
                .onDispose()
                .doOnError { e -> logger.error(e.message, e) }
                .block()
    } catch (ex: Exception) {
        logger.error(ex.message)
    }
}
