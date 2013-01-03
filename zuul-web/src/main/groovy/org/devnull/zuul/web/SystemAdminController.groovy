package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.ZuulService
import org.devnull.zuul.service.security.KeyConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.annotation.Resource
import javax.validation.Valid

import static org.devnull.zuul.web.config.ZuulWebConstants.*

@Controller
class SystemAdminController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService

    @Resource(name = "keyConfigurations")
    List<KeyConfiguration> keyConfigurations

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

    @RequestMapping(value = "/system/keys/create", method = RequestMethod.GET)
    ModelAndView displayCreateKeyForm() {
        return new ModelAndView("/system/createKey", [keyConfigurations:keyConfigurations])
    }

    @RequestMapping(value = "/system/keys/create", method = RequestMethod.POST)
    ModelAndView createKey(@ModelAttribute("createKeyForm") @Valid EncryptionKey key,
                     BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return displayCreateKeyForm()
        }
        zuulService.saveKey(key)
        redirectAttrs.addFlashAttribute(FLASH_ALERT_MESSAGE, "Key ${key.name} Created")
        redirectAttrs.addFlashAttribute(FLASH_ALERT_TYPE, "success")
        return new ModelAndView("redirect:/system/keys")
    }

}
