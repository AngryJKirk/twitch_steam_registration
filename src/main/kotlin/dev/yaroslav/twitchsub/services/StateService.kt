package dev.yaroslav.twitchsub.services

import dev.yaroslav.twitchsub.IntegrationName
import dev.yaroslav.twitchsub.State
import dev.yaroslav.twitchsub.StateData
import org.springframework.stereotype.Component
import javax.servlet.http.HttpSession

@Component
class StateService {

    fun getState(httpSession: HttpSession): Map<IntegrationName, State> {
        val data = getDataInternal(httpSession)
        val stateMap = mutableMapOf(
            IntegrationName.STEAM to State.NONE,
            IntegrationName.TWITCH to State.NONE
        )
        if (data.isSteamError) stateMap[IntegrationName.STEAM] = State.ERROR
        if (data.isTwitchError) stateMap[IntegrationName.TWITCH] = State.ERROR
        if (data.isTwitchError || data.isSteamError) return stateMap
        if (data.steamCode != null) stateMap[IntegrationName.STEAM] = State.OK
        if (data.isTwitchSub == true) stateMap[IntegrationName.TWITCH] = State.OK
        if (data.isTwitchSub == false) stateMap[IntegrationName.TWITCH] = State.ERROR
        return stateMap
    }

    fun updateData(httpSession: HttpSession, stateService: StateData) {
        httpSession.setAttribute("data", stateService)
    }

    fun getData(httpSession: HttpSession): StateData {
        return getDataInternal(httpSession)
    }

    private fun getDataInternal(httpSession: HttpSession): StateData {
        return httpSession.getAttribute("data") as? StateData
            ?: StateData(null, null)
                .also { httpSession.setAttribute("data", it) }
    }
}
