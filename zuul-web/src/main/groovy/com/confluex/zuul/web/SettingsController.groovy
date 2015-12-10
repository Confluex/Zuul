package com.confluex.zuul.web

import com.confluex.util.pagination.HttpRequestPagination
import com.confluex.zuul.data.model.Settings
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.service.ZuulService
import com.confluex.zuul.web.config.ZuulWebConstants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.propertyeditors.StringTrimmerEditor
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class SettingsController {
    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    ZuulService zuulService

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

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
        def group = zuulService.findSettingsGroupByNameAndEnvironment(groupName, environment)
        return new ModelAndView("/settings/entry", [group: group])
    }

    /**
     * Create a new key/value entry for the settings group
     */
    @RequestMapping(value = "/settings/{environment}/{groupName}/create/entry", method = RequestMethod.POST)
    ModelAndView addEntrySubmit(@PathVariable("groupName") String groupName, @PathVariable("environment") String env,
                                @ModelAttribute("formEntry") @Valid SettingsEntry formEntry, BindingResult result) {
        if (result.hasErrors()) {
            return addEntryForm(groupName, env)
        }
        def group = zuulService.findSettingsGroupByNameAndEnvironment(groupName, env)
        zuulService.createEntry(group, formEntry)
        return new ModelAndView("redirect:/settings/${groupName}#${env}")
    }

    /**
     * Show all available settings groups
     * @return
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    ModelAndView index() {
        return new ModelAndView("/settings/index", [settings: zuulService.listSettings()])
    }

    @RequestMapping(value = "/settings/{name}", method = RequestMethod.GET)
    ModelAndView show(@PathVariable("name") String name) {
        def environments = zuulService.listEnvironments()
        def settings = zuulService.getSettingsByName(name)
        def model = [settings: settings, environments: environments]
        return new ModelAndView("/settings/show", model)
    }

    @RequestMapping(value = "/settings/{name}/edit", method = RequestMethod.GET)
    ModelAndView editForm(@PathVariable("name") String name) {
        def settings = zuulService.getSettingsByName(name)
        def model = [settings: settings]
        return new ModelAndView("/settings/edit", model)
    }

    @RequestMapping(value = "/settings/{name}/edit", method = RequestMethod.POST)
    String editFormSubmit(@Valid @ModelAttribute("settings") Settings settings, BindingResult result) {
        if (result.hasErrors()) return "/settings/edit"
        zuulService.save(settings)
        return "redirect:/settings/${settings.name}"
    }

    @RequestMapping(value = "/settings/{name}/delete")
    String delete(RedirectAttributes redirectAttrs, @PathVariable("name") String name) {
        def settings = zuulService.getSettingsByName(name)
        zuulService.delete(settings)
        redirectAttrs.addFlashAttribute(ZuulWebConstants.FLASH_ALERT_MESSAGE, "Settings Deleted")
        redirectAttrs.addFlashAttribute(ZuulWebConstants.FLASH_ALERT_TYPE, "success")
        return "redirect:/"
    }

    /**
     * Redirect to correct tab for settings group
     */
    @RequestMapping(value = "/settings/{env}/{name}", method = RequestMethod.GET)
    String showGroup(@PathVariable("env") String env, @PathVariable("name") String name) {
        return "redirect:/settings/${name}#${env}"
    }


    @RequestMapping(value = "/settings/{environment}/{groupName}/key/change", method = RequestMethod.GET)
    String changeGroupKey(
            @PathVariable("environment") String environment,
            @PathVariable("groupName") String groupName,
            @RequestParam String keyName,
            @CookieValue(value = "PGP_KEY_CHANGE_CONFIRMED", required = false, defaultValue = "false") Boolean pgpConfirmed
    ) {
        def group = zuulService.findSettingsGroupByNameAndEnvironment(groupName, environment)
        def newKey = zuulService.findKeyByName(keyName)
        if (group.key.isPgpKey && group.entries.count { it.encrypted }) {
            return "/error/pgpKeyChange"
        }
        if (newKey.isPgpKey && !pgpConfirmed) {
            return "/settings/pgpKeyConfirm"
        }
        zuulService.changeKey(group, newKey)
        return "redirect:/settings/${groupName}#${environment}"
    }


    @RequestMapping(value = "/settings/search")
    ModelAndView search(@RequestParam("q") String query, HttpServletRequest request) {
        def pagination = new HttpRequestPagination<SettingsEntry>(request)
        def results = zuulService.search(query, pagination)?.groupBy { it.group }
        return new ModelAndView("/settings/search", [query: query, results: results])
    }
}
