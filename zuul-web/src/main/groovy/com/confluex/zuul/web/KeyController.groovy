package com.confluex.zuul.web

import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.service.ZuulService
import com.confluex.zuul.service.security.KeyConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.annotation.Resource
import javax.validation.Valid

import static com.confluex.zuul.web.config.ZuulWebConstants.FLASH_ALERT_MESSAGE
import static com.confluex.zuul.web.config.ZuulWebConstants.FLASH_ALERT_TYPE

@Controller
class KeyController {

    @Autowired
    ZuulService zuulService

    @Resource(name = "keyMetaData")
    Map<String, KeyConfiguration> keyMetaData


    @RequestMapping("/system/keys")
    ModelAndView listKeys() {
        def keys = zuulService.listEncryptionKeys()
        return new ModelAndView("/system/keys/index", [keys: keys, keyMetaData: keyMetaData])
    }

    @RequestMapping(value = "/system/keys/create", method = RequestMethod.GET)
    ModelAndView displayCreateKeyForm() {
        return new ModelAndView("/system/keys/createKey", [keyMetaData: keyMetaData])
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

    @RequestMapping("/system/keys/metadata.json")
    @ResponseBody
    Map<String, KeyConfiguration> renderKeyMetaData() {
        return keyMetaData
    }
}
