package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.util.pagination.HttpRequestPagination
import org.devnull.util.pagination.Pagination
import org.devnull.util.pagination.adapter.DisplayTagPaginatedListAdapter
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.AuditService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest

@Controller
@Slf4j
class AuditController {

    @Autowired
    AuditService auditService

    @ModelAttribute("audits")
    List<SettingsAudit> findAudits(HttpServletRequest request,
                                   @RequestParam(required = false, value = "max", defaultValue = "10") Integer max) {
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        pagination.max = max
        // account for zero based indexing on our Pagination API
        if (pagination.page > 0) pagination.page = pagination.page - 1
        def audits = auditService.findSettingAudits(pagination)
        return new DisplayTagPaginatedListAdapter(audits as Pagination)
    }

    @RequestMapping("/audit")
    String index() {
        return "/audit/index"
    }
}
