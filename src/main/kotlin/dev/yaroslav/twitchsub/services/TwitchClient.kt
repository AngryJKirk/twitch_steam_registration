package dev.yaroslav.twitchsub.services

import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component

@Component
class TwitchClient(val identityProvider: OAuth2IdentityProvider) {

    private val okHttp = OkHttpClient().newBuilder().build()

    fun isSub(code: String): Boolean {
        val cred = identityProvider.getCredentialByCode(code)
            .let(identityProvider::getAdditionalCredentialInformation)
            .orElseThrow()

        val request = Request.Builder()
            .addHeader("Authorization", "OAuth ${cred.accessToken}")
            .addHeader("Accept", "application/vnd.twitchtv.v5+json")
            .url("https://api.twitch.tv/kraken/users/${cred.userId}/subscriptions/orkpod")
            .build()
        return okHttp
            .newCall(request)
            .execute()
            .body
            ?.string()
            ?.isNotEmpty()
            ?: throw RuntimeException("null body")
    }
}
