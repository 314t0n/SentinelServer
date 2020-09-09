package space.sentinel.api.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

open class LoginRequest @JsonCreator constructor(
        @param:JsonProperty("email") val email: String,
        @param:JsonProperty("pass") val pass: String) {

    /**
     * Decodes Base64 encoded password
     */
    fun decodedPassword(): String {
        return String(Base64.getDecoder().decode(pass.toByteArray(StandardCharsets.UTF_8)))
    }

    companion object {
        fun encodePassword(password: String): String {
            return String(Base64.getEncoder().encode(password.toByteArray(StandardCharsets.UTF_8)))
        }
    }
}