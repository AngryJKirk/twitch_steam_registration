package dev.yaroslav.twitchsub.services

import dev.yaroslav.twitchsub.Config
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class SessionDataEncryptor(private val config: Config) {

    fun encrypt(key: String, data: String): String {
        val aesKey = getKey(key)
        return Cipher
            .getInstance("AES")
            .apply { init(Cipher.ENCRYPT_MODE, aesKey) }
            .doFinal(data.encodeToByteArray())
            .let { Base64.getUrlEncoder().encode(it) }
            .let(::String)
    }

    fun decrypt(key: String, data: String): String {
        return Cipher
            .getInstance("AES")
            .apply { init(Cipher.DECRYPT_MODE, getKey(key)) }
            .doFinal(Base64.getUrlDecoder().decode(data))
            .let(::String)
    }

    private fun getKey(key: String): SecretKeySpec {
        val firstPart = config.secretKey.encodeToByteArray()
        val secondPart = key.encodeToByteArray().copyOf(16)
        return SecretKeySpec(firstPart + secondPart, "AES")
    }
}
