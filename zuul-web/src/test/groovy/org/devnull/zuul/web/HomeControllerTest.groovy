package org.devnull.zuul.web

import org.devnull.security.model.User
import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.AuditService
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
