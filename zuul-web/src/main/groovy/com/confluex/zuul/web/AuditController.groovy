package com.confluex.zuul.web

import groovy.util.logging.Slf4j
import com.confluex.security.model.User
import com.confluex.util.pagination.HttpRequestPagination
import com.confluex.util.pagination.Pagination
import com.confluex.util.pagination.adapter.DisplayTagPaginatedListAdapter
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.service.AuditService
import com.confluex.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import javax.servlet.http.HttpServletRequest

import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

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

    @Autowired
    ZuulService zuulService


    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    ModelAndView index(HttpServletRequest request,
                       @RequestParam(required = false, value = "max", defaultValue = "10") Integer max) {
        def filters = findFilters(request)
        def audits = findAudits(request, max)
        def users = findUsers(audits)
        def model = [
                filters: filters,
                audits:audits,
                users:users
        ]
        return new ModelAndView("/audit/index", model)
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

    @RequestMapping(value = "/audit/filter/group/{env}/{name}", method = RequestMethod.GET)
    String filterByGroup(HttpServletRequest request, @PathVariable("env") String env, @PathVariable("name") String name) {
        request.session.setAttribute(SESSION_FILTERS.groupName, name)
        request.session.setAttribute(SESSION_FILTERS.groupEnvironment, env)
        return "redirect:/audit"
    }





    protected List<SettingsAudit> findAudits(HttpServletRequest request, Integer max) {
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        pagination.max = max
        // account for zero based indexing on our Pagination API
        if (pagination.page > 0) pagination.page = pagination.page - 1

        applySessionFilters(request, pagination)

        def audits = auditService.findSettingAudits(pagination)
        return new DisplayTagPaginatedListAdapter(audits as Pagination)
    }


    protected  Map<String, User> findUsers(@ModelAttribute("audits") List<SettingsAudit> audits) {
        return auditService.lookupUsersForAudits(audits)
    }

    protected Map findFilters(HttpServletRequest request) {
        def filters = [:]
        SESSION_FILTERS.each { field, attribute ->
            def value = request.session.getAttribute(attribute)
            if (value) {
                filters[field] = value
            }
        }
        return filters
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
