package org.devnull.zuul.web

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

import static org.devnull.zuul.data.config.ZuulDataConstants.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.support.RequestContextUtils
import javax.servlet.http.HttpServletRequest
import org.springframework.web.servlet.FlashMapManager
import org.slf4j.LoggerFactory

@Controller
class AccountController {
    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService


    @ModelAttribute("user")
    User findAccountFromSecurityContext() {
        return securityService.currentUser
    }

    @RequestMapping(value="/account/profile", method=RequestMethod.GET)
    String profile() {
        return "/account/profile"
    }

    @RequestMapping(value="/account/profile", method=RequestMethod.POST)
    String saveProfile(@ModelAttribute("user") User formUser, RedirectAttributes redirectAttrs) {
        log.info("Saving user profile: {}", formUser)
        def user = securityService.currentUser
        user.firstName = formUser.firstName
        user.lastName = formUser.lastName
        user.email  = formUser.email
        securityService.updateCurrentUser(false)
        redirectAttrs.addFlashAttribute("alert", "Updated Profile")
        redirectAttrs.addFlashAttribute("alertType", "success")
        return "redirect:/account/profile"
    }

    @RequestMapping(value = "/account/register", method = RequestMethod.GET)
    String register() {
        return "/account/register"
    }

    @RequestMapping(value = "/account/register", method = RequestMethod.POST)
    String registerSubmit(@ModelAttribute User user) {
        def currentUser = securityService.currentUser

        currentUser.firstName = user.firstName
        currentUser.lastName = user.lastName
        currentUser.email = user.email
        currentUser.addToRoles(securityService.findRoleByName(ROLE_USER))
        currentUser.roles.removeAll {
            it.name == ROLE_GUEST
        }

        securityService.updateCurrentUser(true)
        return "redirect:/account/welcome"
    }

    @RequestMapping("/account/welcome")
    String welcome() {
        return "/account/welcome"
    }

    @RequestMapping("/account/permissions")
    ModelAndView requestPermissions() {
        return new ModelAndView("/account/permissions", [roles: securityService.listRoles()])
    }

    @RequestMapping("/account/permissions/{roleName}")
    String submitPermissionsRequest(@PathVariable("roleName") String roleName) {
        zuulService.notifyPermissionsRequest(roleName)
        return "/account/requested"
    }
}
