package org.devnull.zuul.web

import groovy.util.logging.Slf4j
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
    static final String SESSION_SETTINGS_ENTRY_KEY = "AUDIT_SETTINGS_ENTRY_KEY"



    @ModelAttribute("filters")
    Map findFilters(HttpServletRequest request) {
        return [
                group: request.session.getAttribute(SESSION_GROUP_ID),
                modifiedBy: request.session.getAttribute(SESSION_MODIFIED_BY),
                key: request.session.getAttribute(SESSION_SETTINGS_ENTRY_KEY)
        ]
    }

    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    String list() {
        return "/audit/index"
    }

    // ----- Group Filters
    @RequestMapping(value = "/audit/filter/add/group", method = RequestMethod.GET)
    String addGroupFilter(HttpServletRequest request, @RequestParam("value") Integer groupId) {
        request.session.setAttribute(SESSION_GROUP_ID, groupId)
        return "redirect:/audit"
    }

    @RequestMapping(value = "/audit/filter/remove/group", method = RequestMethod.GET)
    String removeGroupFilter(HttpServletRequest request) {
        request.session.removeAttribute(SESSION_GROUP_ID)
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

}
