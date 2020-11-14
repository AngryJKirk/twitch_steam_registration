package dev.yaroslav.twitchsub.services

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider
import dev.yaroslav.twitchsub.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TwitchClient(
    private val identityProvider: OAuth2IdentityProvider,
    private val config: Config
) {
    private val logger = LoggerFactory.getLogger(TwitchClient::class.java)
    private val okHttp = OkHttpClient().newBuilder().build()

    fun isSub(code: String): Boolean {
        val cred = identityProvider.getCredentialByCode(code)
            .let(identityProvider::getAdditionalCredentialInformation)
            .orElseThrow()

        return config.channelsToSubscribe
            .firstOrNull { channelName ->
                val result = checkIsSubOfChannel(cred, channelName)
                logger.info("${cred.userName} subscription to $channelName: $result")
                result
            } != null
    }

    private fun checkIsSubOfChannel(cred: OAuth2Credential, channelName: String): Boolean {
        val request = Request.Builder()
            .addHeader("Authorization", "OAuth ${cred.accessToken}")
            .addHeader("Accept", "application/vnd.twitchtv.v5+json")
            .url("https://api.twitch.tv/kraken/users/${cred.userId}/subscriptions/$channelName")
            .build()

        return okHttp
            .newCall(request)
            .execute()
            .body
            ?.string()
            ?.contains("has no subscriptions")
            ?.not()
            ?: throw RuntimeException("null body")
    }
}
