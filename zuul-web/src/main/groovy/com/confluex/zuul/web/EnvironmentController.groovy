package com.confluex.zuul.web

import com.confluex.security.service.SecurityService
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestBody

@Controller
class EnvironmentController {

    @Autowired
    SecurityService securityService

    @Autowired
    ZuulService zuulService


    @RequestMapping(value = "/system/environments")
    ModelAndView list() {
        return new ModelAndView("/system/environments/index", [environments:zuulService.listEnvironments()])
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


    @RequestMapping(value = "/system/environments/sort.json", method=RequestMethod.PUT)
    String sort(@RequestBody List<String> names) {
        zuulService.sortEnvironments(names)
        return "redirect:/system/environments"
    }

}
