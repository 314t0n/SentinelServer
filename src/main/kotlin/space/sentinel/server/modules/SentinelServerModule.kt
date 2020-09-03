package space.sentinel.server.modules

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Singleton
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.KotlinModule
import space.sentinel.controller.ActuatorController
import space.sentinel.controller.NotificationController
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.repository.DeviceRepository
import space.sentinel.service.DeviceService
import space.sentinel.service.NotificationService
import space.sentinel.service.PaginationService
import space.sentinel.translator.DateTimeTranslator
import space.sentinel.translator.DeviceTranslator
import space.sentinel.translator.NotificationTranslator
import space.sentinel.util.QueryParameterResolver

class SentinelServerModule: KotlinModule(){

    override fun configure() {
        bind<ActuatorController>().`in`<Singleton>()
        bind<NotificationService>().`in`<Singleton>()
        bind<NotificationTranslator>().`in`<Singleton>()
        bind<DateTimeTranslator>().`in`<Singleton>()
        bind<DeviceTranslator>().`in`<Singleton>()
        bind<DeviceService>().`in`<Singleton>()
        bind<DeviceRepository>().`in`<Singleton>()
        bind<NotificationController>().`in`<Singleton>()
        bind<NotificationController>().`in`<Singleton>()
        bind<PaginationService>().`in`<Singleton>()
        bind<ApiKeyRepository>().`in`<Singleton>()
        bind<QueryParameterResolver>().`in`<Singleton>()
        bind<ObjectMapper>().toProvider<JacksonObjectMapperProvider>().`in`<Singleton>()
        bind<Config>().toProvider<SentinelConfigProvider>().`in`<Singleton>()
    }

}