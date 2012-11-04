package org.devnull.zuul.web

import org.springframework.stereotype.Controller
import groovy.util.logging.Slf4j
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.web.servlet.ModelAndView
import org.devnull.security.service.AuditService

@Controller
@Slf4j
class AuditController {

    @Autowired
    AuditService auditService

    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    ModelAndView list() {
        def audits = auditService.findAllByEntity(SettingsEntry)
        return new ModelAndView("/audit/index", [audits:audits])
    }
}
