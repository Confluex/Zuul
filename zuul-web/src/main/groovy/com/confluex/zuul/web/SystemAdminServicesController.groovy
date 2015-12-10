package com.confluex.zuul.web

import com.confluex.security.service.SecurityService
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

import javax.servlet.http.HttpServletResponse

@Controller
class SystemAdminServicesController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService

    @RequestMapping(value = "/system/user/role", method = RequestMethod.DELETE)
    void removeRoleFromUser(HttpServletResponse response, @RequestParam("roleId") Integer roleId, @RequestParam("userId") Integer userId) {
        securityService.removeRoleFromUser(roleId, userId)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    @RequestMapping(value = "/system/user/role", method = RequestMethod.POST)
    void addRoleRoleToUser(HttpServletResponse response, @RequestParam("roleId") Integer roleId, @RequestParam("userId") Integer userId) {
        securityService.addRoleToUser(roleId, userId)
        // this really should be a 303 redirect... I'm just not sure to what yet. There is no model for UserRole
        response.status = HttpServletResponse.SC_NO_CONTENT
    }


    @RequestMapping(value = "/system/user/{userId}", method = RequestMethod.DELETE)
    void deleteUser(HttpServletResponse response, @PathVariable("userId") Integer userId) {
        securityService.deleteUser(userId)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    @RequestMapping(value = "/system/keys/default.json", method = RequestMethod.PUT)
    RedirectView setDefaultKey(@RequestBody EncryptionKey key) {
        zuulService.changeDefaultKey(key.name)
        def view = new RedirectView("/system/keys/default.json", true)
        view.statusCode = HttpStatus.SEE_OTHER
        return view
    }

    @RequestMapping(value = "/system/keys/default.json", method = RequestMethod.GET)
    @ResponseBody
    EncryptionKey getDefaultKey() {
        return zuulService.findDefaultKey()
    }

    @RequestMapping(value = "/system/keys/{name}.json", method = RequestMethod.GET)
    @ResponseBody
    EncryptionKey findKeyByName(@PathVariable String name) {
        return zuulService.findKeyByName(name)
    }

    @RequestMapping(value = "/system/keys.json", method = RequestMethod.GET)
    @ResponseBody
    List<EncryptionKey> listKeys() {
        return zuulService.listEncryptionKeys()
    }

    @RequestMapping(value = "/system/keys/{name}.json", method = RequestMethod.PUT)
    @ResponseBody
    EncryptionKey updateKeyByName(@PathVariable String name, @RequestBody EncryptionKey formKey) {
        def key = findKeyByName(name)
        key.description = formKey.description
        key.password = formKey.password
        key.algorithm = formKey.algorithm
        return zuulService.saveKey(key)
    }

    @RequestMapping(value = "/system/keys/{name}.json", method = RequestMethod.DELETE)
    void deleteKeyByName(HttpServletResponse response, @PathVariable String name) {
        zuulService.deleteKey(name)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }
}
