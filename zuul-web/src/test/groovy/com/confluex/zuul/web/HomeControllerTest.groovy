package com.confluex.zuul.web

import com.confluex.security.model.User
import com.confluex.util.pagination.Pagination
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.service.AuditService
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

class HomeControllerTest {

    HomeController controller

    @Before
    void createController() {
        controller = new HomeController(auditService: mock(AuditService))
    }

    @Test
    void shouldContainRecentAuditsInModel() {
        def audits = [new SettingsAudit(id: 1)]
        def users = ["a": new User(userName: "a")]
        when(controller.auditService.findSettingAudits(any(Pagination))).thenReturn(audits)
        when(controller.auditService.lookupUsersForAudits(Matchers.any(List))).thenReturn(users)
        def mv = controller.index()
        assert mv.viewName == "index"
        assert mv.model.audits == audits
        assert mv.model.users == users
    }
}
