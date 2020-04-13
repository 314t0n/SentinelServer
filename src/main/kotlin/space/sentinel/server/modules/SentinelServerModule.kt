package space.sentinel.server.modules

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Singleton
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.KotlinModule
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController
import space.sentinel.service.NotificationService
import space.sentinel.translator.NotificationTranslator

class SentinelServerModule: KotlinModule(){

    override fun configure() {
        bind<ActuatorController>().`in`<Singleton>()
        bind<NotificationService>().`in`<Singleton>()
        bind<NotificationTranslator>().`in`<Singleton>()
        bind<NotificationController>().`in`<Singleton>()
        bind<ObjectMapper>().toProvider<JacksonObjectMapperProvider>().`in`<Singleton>()
        bind<Config>().toProvider<SentinelConfigProvider>().`in`<Singleton>()
    }

}