package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import org.springframework.validation.BindingResult

@Controller
class EnvironmentController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService

    @ModelAttribute("environments")
    List<Environment> findEnvironments() {
        return zuulService.listEnvironments()
    }

    @RequestMapping(value = "/system/environments")
    String list() {
        "/system/environments/index"
    }

    @RequestMapping(value = "/system/environments/delete")
    String delete(@RequestParam("name") String name) {
        zuulService.deleteEnvironment(name)
        return "redirect:/system/environments"
    }

    @RequestMapping(value = "/system/environments/create", method=RequestMethod.POST)
    String create(@ModelAttribute("environment") @Valid Environment environment, BindingResult result) {
        if (result.hasErrors()) {
            return "/system/environments/index"
        }
        zuulService.createEnvironment(environment.name)
        return "redirect:/system/environments"
    }


    @RequestMapping(value = "/system/environments/restrict/toggle")
    String toggleRestriction(@RequestParam("name") String name) {
        zuulService.toggleEnvironmentRestriction(name)
        return "redirect:/system/environments"
    }


}
