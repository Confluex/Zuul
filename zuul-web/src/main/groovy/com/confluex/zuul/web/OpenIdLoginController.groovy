package com.confluex.zuul.web

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@Profile("security-openid")
class OpenIdLoginController {
    @RequestMapping("/login")
    String login() {
        return "/login/openid"
    }
}
