package org.devnull.zuul.web

import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
class SettingsController {

    @Autowired
    ZuulService zuulService

    /**
     * Start the workflow for creating a new settings group
     * @return
     */
    @RequestMapping(value = "/settings/create", method = RequestMethod.GET)
    String newSettingsGroupForm() {
        return "/settings/create"
    }

    /**
     *  Begin a new settings group with no key/vales.
     */
    @RequestMapping(value = "/settings/create/scratch")
    String createFromScratch(@RequestParam("name") String name, @RequestParam("environment") String env) {
        def settingsGroup = zuulService.createEmptySettingsGroup(name, env)
        return "redirect:/settings/${settingsGroup.name}#${settingsGroup.environment.name}"
    }

    /**
     * Display form for creating new settings groups from property file uploads
     */
    @RequestMapping(value = "/settings/create/upload", method = RequestMethod.GET)
    public ModelAndView createFromUpload(@RequestParam("name") String name, @RequestParam("environment") String env) {
        def model = [environment: env, groupName: name]
        return new ModelAndView("/settings/upload", model)
    }

    /**
     * Display form for creating new settings groups from property file uploads
     */
    @RequestMapping(value = "/settings/create/copy", method = RequestMethod.GET)
    public ModelAndView createFromCopy(@RequestParam("name") String name, @RequestParam("environment") String env) {
        def model = [environment: env, groupName: name]
        return new ModelAndView("/settings/copy", model)
    }

    /**
     * Create the new group from the submitted copy
     */
    @RequestMapping(value = "/settings/create/copy", method = RequestMethod.POST)
    public String createFromCopySubmit(@RequestParam("name") String name, @RequestParam("environment") String env,
                                       @RequestParam("search") String search) {
        def match = search =~ /\/(.+)\/(.+)\.properties$/
        if (match) {
            def group = zuulService.findSettingsGroupByNameAndEnvironment(match.group(2), match.group(1))
            zuulService.createSettingsGroupFromCopy(name, env, group)
        }
        // TODO needs some error handling
        return "redirect:/settings/${name}#${env}"
    }

    /**
     * Show the form for a new key/value entry for the settings group
     */
    @RequestMapping(value = "/settings/{environment}/{name}/create/entry", method = RequestMethod.GET)
    ModelAndView addEntryForm(@PathVariable("name") String groupName, @PathVariable("environment") String environment) {
        def model = [groupName: groupName, environment: environment]
        return new ModelAndView("/settings/entry", model)
    }

    /**
     * Create a new key/value entry for the settings group
     */
    @RequestMapping(value = "/settings/{environment}/{name}/create/entry", method = RequestMethod.POST)
    String addEntrySubmit(@PathVariable("name") String name, @PathVariable("environment") String env,
                          SettingsEntry formEntry) {
        def group = zuulService.findSettingsGroupByNameAndEnvironment(name, env)
        def entry = new SettingsEntry(key: formEntry.key, value: formEntry.value, group: group)
        zuulService.save(entry)
        return "redirect:/settings/${name}#${env}"
    }

    /**
     * User interface for editing settings group
     */
    @RequestMapping(value = "/settings/{name}", method = RequestMethod.GET)
    ModelAndView show(@PathVariable("name") String name) {
        def environments = zuulService.listEnvironments()
        def groupsByEnv = [:]
        environments.each { env ->
            groupsByEnv[env] = zuulService.findSettingsGroupByNameAndEnvironment(name, env.name)
            throw new RuntimeException("test")
        }
        def model = [groupsByEnv: groupsByEnv, groupName: name, environments: environments]
        return new ModelAndView("/settings/show", model)
    }


}
