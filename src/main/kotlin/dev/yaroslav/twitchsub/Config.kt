package dev.yaroslav.twitchsub

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("settings")
class Config {
    lateinit var redirectDomain: String
    lateinit var secretKey: String
    lateinit var clientId: String
    lateinit var clientSecret: String
}
