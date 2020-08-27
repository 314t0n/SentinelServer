package space.sentinel.repository

import com.google.inject.Inject
import com.typesafe.config.Config

class ApiKeyRepository @Inject constructor(config: Config){

    private val apiKey = config.getString("apiKey")

    fun isValid(key: String): Boolean {
        println("$apiKey == $key, ${apiKey == key}")
        return apiKey == key
    }

}