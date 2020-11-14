package dev.yaroslav.twitchsub.controllers

import dev.yaroslav.twitchsub.services.StateService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class MainPageController(private val stateService: StateService) {

    @GetMapping("/")
    fun greeting(
        model: Model,
        httpSession: HttpSession
    ): String {
        stateService.getState(httpSession)
            .forEach {
                model.addAttribute(it.key.id, it.value.id)
            }
        return "index"
    }

    @GetMapping("/reset")
    fun reset(httpServletResponse: HttpServletResponse, httpSession: HttpSession) {
        httpSession.invalidate()
        httpServletResponse.sendRedirect("/")
    }
}
