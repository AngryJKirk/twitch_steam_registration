package dev.yaroslav.twitchsub

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("settings")
class Config {
    lateinit var redirectDomain: String
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var channelsToSubscribe: List<String>
    lateinit var regRpWebhookUrl: String
}
