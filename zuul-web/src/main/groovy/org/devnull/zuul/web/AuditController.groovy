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

    static final Map<String, String> SESSION_FILTERS = [
            groupEnvironment: "SESSION_ENVIRONMENT_NAME",
            groupName: "SESSION_GROUP_NAME",
            modifiedBy: "SESSION_MODIFIED_BY",
            settingsKey: "SESSION_SETTINGS_ENTRY_KEY"
    ]


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
        def filters = [:]
        SESSION_FILTERS.each { field, attribute ->
            def value = request.session.getAttribute(attribute)
            if (value) {
                filters[field] = value
            }
        }
        return filters
    }

    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    String index() {
        return "/audit/index"
    }

    @RequestMapping(value = "/audit/filter/add", method = RequestMethod.GET)
    String addSessionFilter(HttpServletRequest request, @RequestParam("value") String value, @RequestParam("field") String field) {
        def attribute = SESSION_FILTERS[field]
        if (attribute) {
            log.info("Adding filter {}={} to session attribute: {}", field, value, attribute)
            request.session.setAttribute(attribute, value)
        }
        else {
            log.warn("Invalid filter attribute: {}. Valid filters: {}", field, SESSION_FILTERS.keySet())
        }
        return "redirect:/audit"
    }

    @RequestMapping(value = "/audit/filter/remove", method = RequestMethod.GET)
    String removeSessionFilter(HttpServletRequest request, @RequestParam("field") String field) {
        def attribute = SESSION_FILTERS[field]
        if (attribute) {
            log.info("Removing filter {}", attribute)
            request.session.removeAttribute(attribute)
        }
        else {
            log.warn("Invalid filter attribute: {}. Valid filters: {}", field, SESSION_FILTERS.keySet())
        }
        return "redirect:/audit"
    }


    /**
     * Looks for potential filters in the session and applies them to the pagination.
     */
    @SuppressWarnings("GroovyAssignabilityCheck")
    protected void applySessionFilters(HttpServletRequest request, HttpRequestPagination<SettingsAudit> pagination) {
        SESSION_FILTERS.each { field, attribute ->
            def value = request.session.getAttribute(attribute)
            if (value) {
                pagination.filter[field] = value
            }
        }
    }


}
