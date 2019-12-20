package space.sentinel

import org.slf4j.LoggerFactory
import reactor.netty.http.server.HttpServer
import space.sentinel.util.ConfigLoaderFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Sentinel")
    val config = ConfigLoaderFactory().load()


    try {
        logger.debug("Setup motion detection")

        val server = HttpServer.create()   // Prepares an HTTP server ready for configuration
                .port(8080)    // Configures the port number as zero, this will let the system pick up
                // an ephemeral port when binding the server
                .route { routes ->
                    // The server will respond only on POST requests
                    // where the path starts with /test and then there is path parameter
                    routes.post("/notification") { request, response ->
                        response.sendString(request.receive()
                                .asString()
                                .map { s -> s + ' ' + request.param("message")}
                                .log("http-server"))
                    }
                }
                .bindNow() // Starts the server in a blocking fashion, and waits for it to finish its initialization

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                server.dispose()
                logger.info("Stop")
            }
        })

        while(true)
            Thread.sleep(500)

        // service port
        // ping

    } catch (ex: Exception) {
        logger.error(ex.message)
    }
}
