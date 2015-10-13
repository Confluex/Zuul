package com.confluex.zuul.web

import com.confluex.security.service.SecurityService
import com.confluex.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class UserController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService


    @RequestMapping("/system/users")
    ModelAndView listUsers() {
        def model = [:]
        model.users = securityService.listUsers()
        model.roles = securityService.listRoles()
        return new ModelAndView("/system/users/index", model)
    }


}
