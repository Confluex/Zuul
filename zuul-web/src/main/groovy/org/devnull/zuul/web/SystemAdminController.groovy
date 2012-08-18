package org.devnull.zuul.web

import org.devnull.security.model.User
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.security.service.SecurityService
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView

@Controller
class SystemAdminController {

    @Autowired
    SecurityService securityService

    @RequestMapping("/admin/system/users")
    ModelAndView listUsers() {
        def model = [:]
        model.users = securityService.listUsers()
        model.roles = securityService.listRoles()
        return new ModelAndView("/system/users", model)
    }
}
