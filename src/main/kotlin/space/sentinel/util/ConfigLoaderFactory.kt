package space.sentinel.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

class ConfigLoaderFactory {

    fun load(configName: String = "dev"): Config {
        return ConfigFactory.load(configName)
                .resolve()
    }
}