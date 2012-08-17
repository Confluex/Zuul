package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sun.plugin.liveconnect.ReplaceMethod
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SystemAdminServicesController {

    @Autowired
    SecurityService securityService

    @RequestMapping(value="/admin/system/user/role", method=RequestMethod.DELETE)
    void removeRoleFromUser(HttpServletResponse response, @RequestParam("roleId") Integer roleId, @RequestParam("userId") Integer userId) {
        securityService.removeRoleFromUser(roleId, userId)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }
}
