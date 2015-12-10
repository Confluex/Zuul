package com.confluex.zuul.web

import groovy.util.logging.Slf4j
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@Slf4j
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
        zuulService.deleteSettingsGroup(group)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    /**
     * View the settings grouped according to folder
     */
    @RequestMapping(value = "/settings/menu.json", method = RequestMethod.GET)
    @ResponseBody
    List<Map> menu(HttpServletRequest request) {
        def menu = []
        def settings = zuulService.listSettings()
        def folders = settings.findAll { it.folder }.groupBy { it.folder }
        def createLeaf = { name ->
            def encoded = URLEncoder.encode(name, 'UTF-8')
            [ name: name, resourceUri: "${request.contextPath}/settings/${encoded}".toString()  ]
        }
        def createNode = { name, leafs ->
            [ name: name, leafs: leafs.sort { it.name }.collect { createLeaf(it.name) } ]
        }
        menu += folders.keySet().sort().collect { createNode(it, folders[it]) }
        menu += settings.findAll { !it.folder }.sort { it.name}.collect { createLeaf(it.name) }
        return menu
    }

    /**
     * View all of the settings groups as JSON
     */
    @RequestMapping(value = "/settings.json", method = RequestMethod.GET)
    @ResponseBody
    List<Map> listJson(HttpServletRequest request) {
        log.debug("Rending JSON for settings groups")
        def results = []
        zuulService.listSettings().each { settings ->
            settings.groups.each {
                results << [
                        name: it.name,
                        environment: it.environment.name,
                        resourceUri: "${request.contextPath}/settings/${it.environment.name}/${it.name}".toString()
                ]
            }
        }

        return results
    }

    /**
     * Render the settings group as JSON
     */
    @RequestMapping(value = "/settings/{environment}/{name}.json", method = RequestMethod.GET)
    @ResponseBody
    Map showByNameAndEnvJson(@PathVariable("name") String name, @PathVariable("environment") String env, HttpServletResponse response) {
        def group = zuulService.findSettingsGroupByNameAndEnvironment(name, env)
        if (!group) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            response.outputStream << "No configuration found for ${env}/${name}"
            return null
        }
        return group as Map
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
        entry.encrypted = formEntry.encrypted
        return zuulService.save(entry)
    }

    /**
     * Delete a key/value entry for a settings group
     */
    @RequestMapping(value = "/settings/entry/{id}.json", method = RequestMethod.DELETE)
    void deleteEntryJson(@PathVariable("id") Integer id, HttpServletResponse response) {
        def entry = zuulService.findSettingsEntry(id)
        zuulService.deleteSettingsEntry(entry)
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    /**
     * Encrypt a value for a key/value entry
     */
    @RequestMapping(value = "/settings/entry/encrypt.json")
    @ResponseBody
    SettingsEntry encrypt(@RequestParam("id") Integer id) {
        def entry = zuulService.encryptSettingsEntryValue(id)
        return zuulService.save(entry, SettingsAudit.AuditType.ENCRYPT)
    }

    /**
     * Encrypt a value for a key/value entry
     */
    @RequestMapping(value = "/settings/entry/decrypt.json")
    @ResponseBody
    SettingsEntry decrypt(@RequestParam("id") Integer id) {
        def entry = zuulService.decryptSettingsEntryValue(id)
        return zuulService.save(entry, SettingsAudit.AuditType.DECRYPT)
    }

}
