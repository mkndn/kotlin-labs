package com.mkdn.kotlinspringchat.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController() {

    @GetMapping("/")
    suspend fun index(): String {
        return "chatrs"
    }
}