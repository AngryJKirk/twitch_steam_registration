package dev.yaroslav.twitchsub.services

import dev.yaroslav.twitchsub.HookResult
import dev.yaroslav.twitchsub.HookType
import dev.yaroslav.twitchsub.StateData

interface SuccessHook {

    fun getHookType(): HookType
    fun processData(data: StateData): HookResult
}

