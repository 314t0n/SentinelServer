package space.sentinel.server.modules

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Singleton
import com.typesafe.config.Config
import dev.misfitlabs.kotlinguice4.KotlinModule
import space.sentinel.controller.*
import space.sentinel.repository.ApiKeyRepository
import space.sentinel.repository.DeviceRepository
import space.sentinel.repository.NotificationRepository
import space.sentinel.repository.UserRepository
import space.sentinel.service.*
import space.sentinel.translator.*
import space.sentinel.util.QueryParameterResolver

class SentinelServerModule: KotlinModule(){

    override fun configure() {
        // Metrics
        bind<ActuatorController>().`in`<Singleton>()

        // Notification
        bind<NotificationController>().`in`<Singleton>()
        bind<NotificationService>().`in`<Singleton>()
        bind<NotificationTranslator>().`in`<Singleton>()
        bind<NotificationRepository>().`in`<Singleton>()

        // Device
        bind<DeviceController>().`in`<Singleton>()
        bind<DeviceService>().`in`<Singleton>()
        bind<DeviceTranslator>().`in`<Singleton>()
        bind<DeviceRepository>().`in`<Singleton>()

        // User
        bind<UserProfileController>().`in`<Singleton>()
        bind<UserService>().`in`<Singleton>()
        bind<UserProfileTranslator>().`in`<Singleton>()
        bind<UserRepository>().`in`<Singleton>()

        // Auth
        bind<AuthenticationController>().`in`<Singleton>()
        bind<AuthService>().`in`<Singleton>()
        bind<LoginTranslator>().`in`<Singleton>()

        // Other
        bind<DateTimeTranslator>().`in`<Singleton>()
        bind<PaginationService>().`in`<Singleton>()
        bind<ApiKeyRepository>().`in`<Singleton>()
        bind<QueryParameterResolver>().`in`<Singleton>()

        bind<ObjectMapper>().toProvider<JacksonObjectMapperProvider>().`in`<Singleton>()
        bind<Config>().toProvider<SentinelConfigProvider>().`in`<Singleton>()
    }

}