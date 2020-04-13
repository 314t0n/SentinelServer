package space.sentinel.server.modules

import com.google.inject.Provider
import com.typesafe.config.Config
import space.sentinel.util.ConfigLoaderFactory
import java.util.*

class SentinelConfigProvider : Provider<Config> {
    override fun get(): Config {
        val env = Optional.ofNullable(System.getProperty("CONFIG_ENVIRONMENT")).orElse("dev")
        return ConfigLoaderFactory().load(env)
    }
}