package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletResponse

@Controller
class SystemAdminServicesController {

    @Autowired
    SecurityService securityService

    @RequestMapping(value = "/admin/system/user/role", method = RequestMethod.DELETE)
    void removeRoleFromUser(HttpServletResponse response, @RequestParam("roleId") Integer roleId, @RequestParam("userId") Integer userId) {
        securityService.removeRoleFromUser(roleId, userId)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    @RequestMapping(value = "/admin/system/user/role", method = RequestMethod.POST)
    void addRoleRoleToUser(HttpServletResponse response, @RequestParam("roleId") Integer roleId, @RequestParam("userId") Integer userId) {
        securityService.addRoleToUser(roleId, userId)
        // this really should be a 302 redirect... I'm just not sure to what yet. There is no model for UserRole
        response.status = HttpServletResponse.SC_NO_CONTENT
    }
}
