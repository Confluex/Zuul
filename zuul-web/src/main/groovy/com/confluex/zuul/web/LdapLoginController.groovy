package com.confluex.zuul.web

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@Profile("security-ldap")
class LdapLoginController {
    @RequestMapping("/login")
    String login() {
        return "/login/form"
    }
}
