package dev.yaroslav.twitchsub

import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider
import com.github.twitch4j.auth.providers.TwitchIdentityProvider
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@SpringBootApplication(
    exclude = [DataSourceAutoConfiguration::class, DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class]
)
@EnableConfigurationProperties(Config::class)

class Main(private val config: Config) {

    @Bean
    fun identityProvider(): OAuth2IdentityProvider {
        return TwitchIdentityProvider(
            config.clientId,
            config.clientSecret,
            "${config.redirectDomain}/twitch/done"
        )
    }
}

fun main() {

    val app = SpringApplication(Main::class.java)
    app.run()
}
