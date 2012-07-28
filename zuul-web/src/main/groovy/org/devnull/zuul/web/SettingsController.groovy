package org.devnull.zuul.web

import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.ResponseBody
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsGroup

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

    @RequestMapping(value="/settings/{name}")
     ModelAndView show(@PathVariable("name") String name) {
        def groups = zuulService.findSettingsGroupByName(name)
        def groupsByEnv = groups.groupBy { it.environment.name }
        return new ModelAndView("/settings/show", [groupsByEnv:groupsByEnv, groupName:name, environments: groupsByEnv.keySet()])
    }

    @RequestMapping(value="/settings.json")
    @ResponseBody
    List<SettingsGroup> listJson() {
        return zuulService.listSettingsGroups()
    }
}
