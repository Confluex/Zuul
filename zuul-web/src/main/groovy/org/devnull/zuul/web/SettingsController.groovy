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


    @RequestMapping(value = "/settings/new", method = RequestMethod.GET)
    String newSettingsGroupForm() {
        return "/settings/new"
    }

    @RequestMapping(value = "/settings/{environment}/{name}.properties", method = RequestMethod.GET)
    void renderPropertiesByNameAndEnv(HttpServletResponse response, @PathVariable("name") String name, @PathVariable("environment") String env) {
        def properties = zuulService.findSettingsGroupByNameAndEnvironment(name, env) as Properties
        response.setContentType("text/plain")
        properties.store(response.outputStream, "Generated from Zuul  with parameters: name=${name}, environment=${env}")
    }

    @RequestMapping(value = "/settings/{environment}/{name}", method=RequestMethod.POST)
    ModelAndView createFromScratch(@PathVariable("name") String name, @PathVariable("environment") String env) {

    }

    @RequestMapping(value = "/settings/{name}", method=RequestMethod.GET)
    ModelAndView show(@PathVariable("name") String name) {
        def environments = zuulService.listEnvironments()
        def groupsByEnv = [:]
        environments.each { env ->
            groupsByEnv[env] = zuulService.findSettingsGroupByNameAndEnvironment(name, env.name)
        }
        def model = [groupsByEnv: groupsByEnv, groupName: name, environments: environments]
        return new ModelAndView("/settings/show", model)
    }

    @RequestMapping(value = "/settings.json")
    @ResponseBody
    List<SettingsGroup> listJson() {
        return zuulService.listSettingsGroups()
    }

    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.GET)
    @ResponseBody
    SettingsEntry showEntryJson(@PathVariable("id") Integer id) {
        return zuulService.findSettingsEntry(id)
    }

    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.PUT)
    @ResponseBody
    SettingsEntry updateEntryJson(@PathVariable("id") Integer id, @RequestBody SettingsEntry formEntry) {
        def entry = zuulService.findSettingsEntry(id)
        entry.key = formEntry.key
        entry.value = formEntry.value
        return zuulService.save(entry)
    }

    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.DELETE)
    @ResponseBody()
    String deleteEntryJson(@PathVariable("id") Integer id, HttpServletResponse response) {
        zuulService.deleteSettingsEntry(id)
        response.status = 204
        return ""
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
