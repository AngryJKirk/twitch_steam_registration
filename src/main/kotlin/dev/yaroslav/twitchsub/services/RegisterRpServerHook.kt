package dev.yaroslav.twitchsub.services

import dev.yaroslav.twitchsub.Config
import dev.yaroslav.twitchsub.HookResult
import dev.yaroslav.twitchsub.HookResultState
import dev.yaroslav.twitchsub.HookType
import dev.yaroslav.twitchsub.StateData
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RegisterRpServerHook(private val config: Config) : SuccessHook {
    private val logger = LoggerFactory.getLogger(RegisterRpServerHook::class.java)
    private val okHttp = OkHttpClient().newBuilder().build()
    override fun getHookType() = HookType.RP_REGISTRATION

    override fun processData(data: StateData): HookResult {

        if (data.hooksDone.contains(getHookType())) {
            return HookResult(HookResultState.NOTHING_DONE)
        }

        return if (data.isTwitchSub == true && data.steamCode != null) {
            val request = Request
                .Builder()
                .url("${config.regRpWebhookUrl}?steam=steam:${data.steamCode}")
                .get()
                .build()

            runCatching {
                val response = okHttp.newCall(request).execute()
                if (response.code == 200) {
                    HookResult(HookResultState.OK)
                } else {
                    HookResult(HookResultState.ERROR, message = "Got ${response.code} response code")
                }
            }.getOrElse { exception ->
                logger.info("Some error happen", exception)
                HookResult(HookResultState.ERROR, exception.message)
            }
        } else HookResult(HookResultState.NOTHING_DONE)
    }
}
