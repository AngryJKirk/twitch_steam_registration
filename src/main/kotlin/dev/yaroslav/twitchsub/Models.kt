package dev.yaroslav.twitchsub

enum class State(val id: String) {
    OK("ok"),
    ERROR("error"),
    NONE("none")
}

enum class IntegrationName(val id: String) {
    TWITCH("twitch"),
    STEAM("steam")
}

data class StateData(val steamCode: String?, val isTwitchSub: Boolean?, val isSteamError: Boolean = false, val isTwitchError: Boolean = false)
