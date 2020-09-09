package space.sentinel.server.modules

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.google.inject.Provider

class JacksonObjectMapperProvider : Provider<ObjectMapper> {

    override fun get(): ObjectMapper {
        return ObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(JavaTimeModule())
                .registerModule(Jdk8Module())
                .registerModule(ParameterNamesModule())
    }
}