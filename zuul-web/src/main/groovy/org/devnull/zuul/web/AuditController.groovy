package org.devnull.zuul.web

import groovy.util.logging.Slf4j
import org.devnull.util.pagination.HttpRequestPagination
import org.devnull.util.pagination.Pagination
import org.devnull.util.pagination.adapter.DisplayTagPaginatedListAdapter
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.ZuulService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

import javax.servlet.http.HttpServletRequest

@Controller
@Slf4j
class AuditController {

    @Autowired
    ZuulService zuulService

    @ModelAttribute("audits")
    List<SettingsAudit> findAudits(HttpServletRequest request) {
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        def audits = zuulService.findSettingAudits(pagination)
        return new DisplayTagPaginatedListAdapter(audits as Pagination)
    }

    @RequestMapping("/audit")
    String index() {
        return "/audit/index"
    }
}
