package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SystemAdminController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService

    @RequestMapping("/system/users")
    ModelAndView listUsers() {
        def model = [:]
        model.users = securityService.listUsers()
        model.roles = securityService.listRoles()
        return new ModelAndView("/system/users", model)
    }

    @RequestMapping("/system/keys")
    ModelAndView listKeys() {
        def model = [:]
        model.keys = zuulService.listEncryptionKeys()
        return new ModelAndView("/system/keys", model)
    }

    @RequestMapping(value = "/system/keys/default/{keyName}")
    String setDefaultKey(HttpServletResponse response, @PathVariable("keyName") String keyName) {
        zuulService.changeDefaultKey(keyName)
        return "redirect:/system/keys"
    }
}
