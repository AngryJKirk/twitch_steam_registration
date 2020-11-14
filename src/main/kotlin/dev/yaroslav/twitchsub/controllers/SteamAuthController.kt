package dev.yaroslav.twitchsub.controllers

import dev.yaroslav.twitchsub.Config
import dev.yaroslav.twitchsub.services.StateService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/steam")
class SteamAuthController(config: Config, private val stateService: StateService) {
    private val logger = LoggerFactory.getLogger(SteamAuthController::class.java)
    private val redirectUrl =
        "https://steamcommunity.com/openid/login?openid.ns=http://specs.openid.net/auth/2.0&openid.mode=checkid_setup&openid.return_to=${config.redirectDomain}/steam/done&openid.realm=http://${config.redirectDomain}&openid.ns.sreg=http://openid.net/extensions/sreg/1.1&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"

    @GetMapping
    fun getRedirect(httpServletResponse: HttpServletResponse) {
        httpServletResponse.addHeader("Cache-Control", "no-cache ")
        httpServletResponse.sendRedirect(redirectUrl)
    }

    @GetMapping("/done")
    fun doneAuth(
        @RequestParam("openid.identity", required = false) steamId: String?,
        httpSession: HttpSession,
        httpServletResponse: HttpServletResponse
    ) {
        val state = stateService.getData(httpSession)
        if (steamId == null) {
            stateService.updateData(httpSession, state.copy(isSteamError = true, steamCode = null))
            httpServletResponse.sendRedirect("/")
            return
        }
        try {
            val steamCode = java.lang.Long.toHexString(steamId.split("/").last().toLong())
            stateService.updateData(httpSession, state.copy(isSteamError = false, steamCode = steamCode))
        } catch (e: Exception) {
            logger.error("Steam error", e)
        }
        httpServletResponse.sendRedirect("/")
    }
}
