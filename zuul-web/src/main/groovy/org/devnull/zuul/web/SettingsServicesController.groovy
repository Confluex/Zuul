package org.devnull.zuul.web

import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletResponse

import org.springframework.web.bind.annotation.*

@Controller
class SettingsServicesController {

    @Autowired
    ZuulService zuulService



    /**
     * Create a new settings group with entries from an uploaded properties file. Note that this must
     * be multipart form encoded until I can figure out how to get browsers to simply post an inputstream
     * of the raw file bytes. Need to look closer at XmlHTTPRequest and FileReader APIs. This would likely
     * break any compatibility with IE (but will anyone care/notice?).
     */
    @RequestMapping(value = "/settings/{environment}/{name}.properties", method = RequestMethod.POST)
    public String createFromProperties(@RequestParam("file") MultipartFile file, @PathVariable("name") String name, @PathVariable("environment") String env) {
        zuulService.createSettingsGroupFromPropertiesFile(name, env, file.inputStream)
        return "redirect:/settings/${name}#${env}"
    }

    /**
     * Render a java properties file usable in an application.
     */
    @RequestMapping(value = "/settings/{environment}/{name}.properties", method = RequestMethod.GET)
    void renderPropertiesByNameAndEnv(HttpServletResponse response, @PathVariable("name") String name, @PathVariable("environment") String env) {
        def properties = zuulService.findSettingsGroupByNameAndEnvironment(name, env) as Properties
        response.setContentType("text/plain")
        properties.store(response.outputStream, "Generated from Zuul  with parameters: name=${name}, environment=${env}")
    }

    /**
     * Delete a properties file
     */
    @RequestMapping(value = "/settings/{environment}/{name}.properties", method = RequestMethod.DELETE)
    void deletePropertiesByNameAndEnv(HttpServletResponse response, @PathVariable("name") String name, @PathVariable("environment") String env) {
        def group = zuulService.findSettingsGroupByNameAndEnvironment(name, env)
        zuulService.deleteSettingsGroup(group.id)
        response.status = 204
    }



    /**
     * View all of the settings groups as JSON
     */
    @RequestMapping(value = "/settings.json")
    @ResponseBody
    List<SettingsGroup> listJson(@RequestParam(required=false, value="deepFetch", defaultValue="false") Boolean deepFetch) {
        def groups = zuulService.listSettingsGroups()
        return deepFetch ? groups : groups.collect { it as Map } as List<SettingsGroup>
    }

    /**
     * View a specific entry in JSON
     */
    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.GET)
    @ResponseBody
    SettingsEntry showEntryJson(@PathVariable("id") Integer id) {
        return zuulService.findSettingsEntry(id)
    }

    /**
     * Replace a key/value entry for a settings group with new values
     */
    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.PUT)
    @ResponseBody
    SettingsEntry updateEntryJson(@PathVariable("id") Integer id, @RequestBody SettingsEntry formEntry) {
        def entry = zuulService.findSettingsEntry(id)
        entry.key = formEntry.key
        entry.value = formEntry.value
        return zuulService.save(entry)
    }

    /**
     * Delete a key/value entry for a settings group
     */
    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.DELETE)
    @ResponseBody()
    String deleteEntryJson(@PathVariable("id") Integer id, HttpServletResponse response) {
        zuulService.deleteSettingsEntry(id)
        response.status = 204
        return ""
    }

    /**
     * Encrypt a value for a key/value entry
     */
    @RequestMapping(value = "/settings/entry/encrypt.json")
    @ResponseBody
    SettingsEntry encrypt(@RequestParam("id") Integer id) {
        return zuulService.encryptSettingsEntryValue(id)
    }

    /**
     * Encrypt a value for a key/value entry
     */
    @RequestMapping(value = "/settings/entry/decrypt.json")
    @ResponseBody
    SettingsEntry decrypt(@RequestParam("id") Integer id) {
        return zuulService.decryptSettingsEntryValue(id)
    }
}
