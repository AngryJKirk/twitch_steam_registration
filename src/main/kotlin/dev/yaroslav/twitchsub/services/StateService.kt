package dev.yaroslav.twitchsub.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.yaroslav.twitchsub.IntegrationName
import dev.yaroslav.twitchsub.State
import dev.yaroslav.twitchsub.StateData
import org.springframework.stereotype.Component
import javax.servlet.http.HttpSession

@Component
class StateService(private val encryptor: SessionDataEncryptor) {

    val objectMapper = jacksonObjectMapper()
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
        val dataJson = objectMapper.writeValueAsString(stateService)
        val encryptedData = encryptor.encrypt(httpSession.id, dataJson)
        httpSession.setAttribute("data", encryptedData)
    }

    fun getData(httpSession: HttpSession): StateData {
        return getDataInternal(httpSession)
    }

    private fun getDataInternal(httpSession: HttpSession): StateData {
        val data = httpSession.getAttribute("data")
        return if (data == null) {
            val dataObj = StateData(null, null)
            val dataJson = objectMapper.writeValueAsString(dataObj)
            val encryptedData = encryptor.encrypt(httpSession.id, dataJson)
            httpSession.setAttribute("data", encryptedData)
            dataObj
        } else {
            val decryptedData = encryptor.decrypt(httpSession.id, data as String)
            objectMapper.readValue(decryptedData, StateData::class.java)
        }
    }
}
