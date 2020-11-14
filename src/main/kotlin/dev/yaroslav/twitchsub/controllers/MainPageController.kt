package dev.yaroslav.twitchsub.controllers

import dev.yaroslav.twitchsub.HookResultState
import dev.yaroslav.twitchsub.services.StateService
import dev.yaroslav.twitchsub.services.SuccessHook
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class MainPageController(
    private val stateService: StateService,
    private val successHooks: List<SuccessHook>
) {

    @GetMapping("/")
    fun greeting(
        model: Model,
        httpSession: HttpSession
    ): String {
        stateService.getState(httpSession)
            .forEach {
                model.addAttribute(it.key.id, it.value.id)
            }
        val data = stateService.getData(httpSession)
        successHooks.forEach {
            val hookResult = it.processData(data)
            if (hookResult.hookState == HookResultState.OK) {
                val newData = data.copy(hooksDone = data.hooksDone.plus(it.getHookType()))
                stateService.updateData(httpSession, newData)
            }
        }
        return "index"
    }

    @GetMapping("/reset")
    fun reset(httpServletResponse: HttpServletResponse, httpSession: HttpSession) {
        httpSession.invalidate()
        httpServletResponse.sendRedirect("/")
    }
}
