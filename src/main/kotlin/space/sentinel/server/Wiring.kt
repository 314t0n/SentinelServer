package space.sentinel.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator
import space.sentinel.util.ConfigLoaderFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

class Wiring(env: String) {
    val config = ConfigLoaderFactory().load(env)
    val objectmapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .registerModule(Jdk8Module())
            .registerModule(ParameterNamesModule())

    val actuatorController = ActuatorController()
    val notificationController = NotificationController(NotificationService(), NotificationTranslator(objectmapper))
}