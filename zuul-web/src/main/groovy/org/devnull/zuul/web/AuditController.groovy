package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.security.audit.AuditPagination
import org.devnull.security.service.AuditService
import org.devnull.zuul.data.model.SettingsEntry
import org.hibernate.envers.query.AuditEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@Slf4j
class AuditController {

    @Autowired
    AuditService auditService


    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    ModelAndView list(@RequestParam(required = false) Integer groupId) {
        def pagination = new AuditPagination()
        if (groupId) {
            pagination.filter << AuditEntity.relatedId("group").eq(groupId)
        }
        def audits = auditService.findAllByEntity(SettingsEntry, pagination)
        def users = auditService.collectUsersFromRevisions(audits)
        return new ModelAndView("/audit/index", [audits: audits, users: users])
    }

}
