package org.devnull.zuul.web

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class AccountController {

    @Autowired
    SecurityService securityService

    @ModelAttribute("user")
    User findAccountFromSecurityContext() {
        return securityService.currentUser
    }

    @RequestMapping("/profile")
    String profile() {
        return "/profile"
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    String register() {
        return "/register"
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    String registerSubmit(@ModelAttribute User user) {
        securityService.save(user)
        securityService.register(user.id)
        return "redirect:/welcome"
    }

    @RequestMapping("/welcome")
    String welcome() {
        return "welcome"
    }
}
