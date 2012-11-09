package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.security.audit.AuditPagination
import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User
import org.devnull.security.service.AuditService
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.SettingsEntry
import org.hibernate.envers.query.AuditEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest

@Controller
@Slf4j
class AuditController {

    static final String SESSION_GROUP_ID = "AUDIT_GROUP_ID"
    static final String SESSION_MODIFIED_BY = "AUDIT_MODIFIED_BY"

    @Autowired
    AuditService auditService


    @ModelAttribute("audits")
    List<AuditRevision<SettingsEntry>> findAudits(HttpServletRequest request) {
        def pagination = new AuditPagination()
        def groupId = request.session.getAttribute(SESSION_GROUP_ID)
        def modifiedBy = request.session.getAttribute(SESSION_MODIFIED_BY)
        if (groupId) {
            pagination.filter << AuditEntity.relatedId("group").eq(groupId)
        }
        if (modifiedBy) {
            pagination.filter << AuditEntity.revisionProperty("modifiedBy").eq(modifiedBy)
        }
        return auditService.findAllByEntity(SettingsEntry, pagination)
    }

    @ModelAttribute("users")
    Map<String, User> findUsers(@ModelAttribute("audits") List<AuditRevision> audits) {
        return auditService.collectUsersFromRevisions(audits)
    }

    @ModelAttribute("filters")
    Map findFilters(HttpServletRequest request) {
        return [
                group: request.session.getAttribute(SESSION_GROUP_ID),
                modifiedBy: request.session.getAttribute(SESSION_MODIFIED_BY)
        ]
    }

    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    String list() {
        return "/audit/index"
    }

    @RequestMapping(value = "/audit/filter/add/group", method = RequestMethod.GET)
    String addGroupFilter(HttpServletRequest request, @RequestParam("groupId") Integer groupId) {
        request.session.setAttribute(SESSION_GROUP_ID, groupId)
        return "redirect:/audit"
    }

    @RequestMapping(value = "/audit/filter/add/modifiedBy", method = RequestMethod.GET)
    String addModifiedByFilter(HttpServletRequest request, @RequestParam("modifiedBy") String modifiedBy) {
        request.session.setAttribute(SESSION_MODIFIED_BY, modifiedBy)
        return "redirect:/audit"
    }


    @RequestMapping(value = "/audit/filter/remove/group", method = RequestMethod.GET)
    String removeGroupFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_GROUP_ID)
        return "redirect:/audit"
    }

    @RequestMapping(value = "/audit/filter/remove/modifiedBy", method = RequestMethod.GET)
    String removeModifiedByFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_MODIFIED_BY)
        return "redirect:/audit"
    }
}
