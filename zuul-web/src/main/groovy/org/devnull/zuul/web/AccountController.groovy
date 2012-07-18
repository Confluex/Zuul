package org.devnull.zuul.web

import javax.servlet.http.HttpServletRequest
import org.devnull.security.model.User
import org.devnull.security.service.OpenIdRegistrationHandler
import org.devnull.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
class AccountController {

    @Autowired
    SecurityService securityService

    @RequestMapping("/login")
    String login() {
        return "/login"
    }

    @RequestMapping("/profile")
    String profile() {
        return "/profile"
    }

    @RequestMapping(value="/register", method=RequestMethod.GET)
    ModelAndView register(HttpServletRequest request) {
        def user = request.session.getAttribute(OpenIdRegistrationHandler.SESSION_OPENID_TEMP_USER)
        return new ModelAndView("/register", [user:user])
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    ModelAndView registerSubmit(HttpServletRequest request, User user) {
        def openIdToken = retrieveTempUserFromSession(request).openId
        securityService.registerNewOpenIdUser(openIdToken, user)
        return new ModelAndView("redirect:/login", [user:user])
    }

    protected User retrieveTempUserFromSession(HttpServletRequest request) {
        def user = request.session.getAttribute(OpenIdRegistrationHandler.SESSION_OPENID_TEMP_USER)
        return user as User
    }
}
