package org.devnull.zuul.web

import groovy.json.JsonSlurper
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
@Profile("security-openid")
class OpenIdLoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        //return back to index.jsp
        ModelAndView model = new ModelAndView("/login/openid");
        model.addObject("providers",  getProviders());
        return model;

    }

    def getProviders(){
        def json = new ClassPathResource("security/OpenIdProviders.json").inputStream.text
        def slurper = new JsonSlurper()
        return slurper.parseText(json).openIdProviders
    }
}
