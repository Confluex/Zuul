package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.security.service.AuditService
import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.devnull.security.service.SecurityService
import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User

@Controller
@Slf4j
class AuditController {

    @Autowired
    AuditService auditService


    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    ModelAndView list() {
        def audits = auditService.findAllByEntity(SettingsEntry)
        def users = auditService.collectUsersFromRevisions(audits)
        return new ModelAndView("/audit/index", [audits: audits, users: users])
    }

}
