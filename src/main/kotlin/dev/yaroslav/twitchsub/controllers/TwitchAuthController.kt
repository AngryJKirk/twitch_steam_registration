package dev.yaroslav.twitchsub.controllers

import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider
import dev.yaroslav.twitchsub.services.StateService
import dev.yaroslav.twitchsub.services.TwitchClient
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/twitch")
class TwitchAuthController(
    private val twitchClient: TwitchClient,
    private val identityProvider: OAuth2IdentityProvider,
    private val stateService: StateService
) {

    private val logger = LoggerFactory.getLogger(TwitchAuthController::class.java)

    @GetMapping
    fun getAuthRedirect(
        httpServletResponse: HttpServletResponse
    ) {
        val url = identityProvider.getAuthenticationUrl(listOf("user_subscriptions"), null)
        httpServletResponse.setHeader("Cache-Control", "no-cache ")
        httpServletResponse.setHeader("Location", url)
        httpServletResponse.status = 302
    }

    @GetMapping("/done")
    fun doneAuth(
        @RequestParam(required = false) code: String?,
        httpSession: HttpSession,
        httpServletResponse: HttpServletResponse
    ) {
        val data = stateService.getData(httpSession)
        if (code == null) {
            stateService.updateData(httpSession, data.copy(isTwitchError = true, isTwitchSub = null))
            httpServletResponse.sendRedirect("/")
            return
        }
        try {
            val isSub = twitchClient.isSub(code)
            stateService.updateData(httpSession, data.copy(isTwitchError = false, isTwitchSub = isSub))
        } catch (e: Exception) {
            logger.error("Twitch error", e)
            stateService.updateData(httpSession, data.copy(isTwitchError = true, isTwitchSub = null))
        }
        httpServletResponse.sendRedirect("/")
    }
}
