package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.security.model.User
import org.devnull.util.pagination.HttpRequestPagination
import org.devnull.util.pagination.Pagination
import org.devnull.util.pagination.adapter.DisplayTagPaginatedListAdapter
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.AuditService
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

    static final String SESSION_GROUP_NAME = "AUDIT_GROUP_ID"
    static final String SESSION_MODIFIED_BY = "AUDIT_MODIFIED_BY"
    static final String SESSION_SETTINGS_ENTRY_KEY = "AUDIT_SETTINGS_ENTRY_KEY"

    @Autowired
    AuditService auditService

    @ModelAttribute("audits")
    List<SettingsAudit> findAudits(HttpServletRequest request,
                                   @RequestParam(required = false, value = "max", defaultValue = "10") Integer max) {
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        pagination.max = max
        // account for zero based indexing on our Pagination API
        if (pagination.page > 0) pagination.page = pagination.page - 1

        applySessionFilters(request, pagination)

        def audits = auditService.findSettingAudits(pagination)
        return new DisplayTagPaginatedListAdapter(audits as Pagination)
    }


    @ModelAttribute("users")
    Map<String, User> findUsers(@ModelAttribute("audits") List<SettingsAudit> audits) {
        return auditService.lookupUsersForAudits(audits)
    }

    @ModelAttribute("filters")
    Map findFilters(HttpServletRequest request) {
        return [
                group: request.session.getAttribute(SESSION_GROUP_NAME),
                modifiedBy: request.session.getAttribute(SESSION_MODIFIED_BY),
                key: request.session.getAttribute(SESSION_SETTINGS_ENTRY_KEY)
        ]
    }

    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    String index() {
        return "/audit/index"
    }

     // ----- Group Filters
    @RequestMapping(value = "/audit/filter/add/group", method = RequestMethod.GET)
    String addGroupFilter(HttpServletRequest request, @RequestParam("value") String group) {
        request.session.setAttribute(SESSION_GROUP_NAME, group)
        return "redirect:/audit"
    }
    @RequestMapping(value = "/audit/filter/remove/group", method = RequestMethod.GET)
    String removeGroupFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_GROUP_NAME)
        return "redirect:/audit"
    }


    // ----- Modified By Filters
    @RequestMapping(value = "/audit/filter/add/modifiedBy", method = RequestMethod.GET)
    String addModifiedByFilter(HttpServletRequest request, @RequestParam("value") String modifiedBy) {
        request.session.setAttribute(SESSION_MODIFIED_BY, modifiedBy)
        return "redirect:/audit"
    }
    @RequestMapping(value = "/audit/filter/remove/modifiedBy", method = RequestMethod.GET)
    String removeModifiedByFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_MODIFIED_BY)
        return "redirect:/audit"
    }


    // ----- Key Filters
    @RequestMapping(value = "/audit/filter/add/key", method = RequestMethod.GET)
    String addKeyFilter(HttpServletRequest request, @RequestParam("value") String key) {
        request.session.setAttribute(SESSION_SETTINGS_ENTRY_KEY, key)
        return "redirect:/audit"
    }
    @RequestMapping(value = "/audit/filter/remove/key", method = RequestMethod.GET)
    String removeKeyFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_SETTINGS_ENTRY_KEY)
        return "redirect:/audit"
    }

    /* ---------- Internals --------- */

    /**
     * Looks for potential filters in the session and applies them to the pagination.
     */
    @SuppressWarnings("GroovyAssignabilityCheck")
    protected void applySessionFilters(HttpServletRequest request, HttpRequestPagination<SettingsAudit> pagination) {
        def groupFilter = request.session.getAttribute(SESSION_GROUP_NAME)
        def modifiedByFilter  = request.session.getAttribute(SESSION_MODIFIED_BY)
        def keyFilter  = request.session.getAttribute(SESSION_SETTINGS_ENTRY_KEY)
        if (groupFilter) {
            pagination.filter.groupName = groupFilter
        }
        if (modifiedByFilter) {
            pagination.filter.modifiedBy = modifiedByFilter
        }
        if (keyFilter) {
            pagination.filter.settingsKey = keyFilter
        }
    }


}
