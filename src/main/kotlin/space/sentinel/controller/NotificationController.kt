package space.sentinel.controller

import reactor.netty.http.server.HttpServerRoutes

class NotificationController() {
    private val notificationRoute = "/notification"

    fun register(routes: HttpServerRoutes) {
        routes.post(notificationRoute) { request, response ->
            response.sendString(request.receive().asString().map { s -> request.param("message") })
        }
    }

}