package org.devnull.zuul.web

import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletResponse

import org.springframework.web.bind.annotation.*

@Controller
class SettingsController {

    @Autowired
    ZuulService zuulService


    @RequestMapping(value = "/settings/{environment}/{name}.properties", method = RequestMethod.GET)
    void renderPropertiesByNameAndEnv(HttpServletResponse response, @PathVariable("name") String name, @PathVariable("environment") String env) {
        def properties = zuulService.findSettingsGroupByNameAndEnvironment(name, env) as Properties
        response.setContentType("text/plain")
        properties.store(response.outputStream, "Generated from Zuul  with parameters: name=${name}, environment=${env}")
    }

    @RequestMapping(value = "/settings/{name}")
    ModelAndView show(@PathVariable("name") String name) {
        def groups = zuulService.findSettingsGroupByName(name)
        def groupsByEnv = groups.groupBy { it.environment.name }
        return new ModelAndView("/settings/show", [groupsByEnv: groupsByEnv, groupName: name, environments: groupsByEnv.keySet()])
    }

    @RequestMapping(value = "/settings.json")
    @ResponseBody
    List<SettingsGroup> listJson() {
        return zuulService.listSettingsGroups()
    }

    @RequestMapping(value = "/settings/entry/encrypt.json")
    @ResponseBody
    SettingsEntry encrypt(@RequestParam("id") Integer id) {
        return zuulService.encryptSettingsEntryValue(id)
    }

    @RequestMapping(value = "/settings/entry/decrypt.json")
    @ResponseBody
    SettingsEntry decrypt(@RequestParam("id") Integer id) {
        return zuulService.decryptSettingsEntryValue(id)
    }
}
