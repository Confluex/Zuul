package com.confluex.zuul.web

import com.confluex.util.pagination.SimplePagination
import com.confluex.util.pagination.Sort
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.service.AuditService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController {
    @Autowired
    AuditService auditService

    @RequestMapping("/")
    ModelAndView index() {
        def pagination = new SimplePagination<SettingsAudit>(max: 5, sorts: [new Sort(field: "modifiedDate", direction: Sort.DESC)])
        def audits = auditService.findSettingAudits(pagination)
        def users = auditService.lookupUsersForAudits(audits)
        return new ModelAndView("index", [audits: audits, users: users])
    }
}
