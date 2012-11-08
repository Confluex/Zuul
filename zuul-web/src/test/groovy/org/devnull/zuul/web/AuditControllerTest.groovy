package org.devnull.zuul.web

import org.devnull.security.audit.AuditPagination
import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User
import org.devnull.security.model.UserRevision
import org.devnull.security.service.AuditService
import org.devnull.zuul.data.model.SettingsEntry
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController(auditService: mock(AuditService))
    }

    @Test
    void shouldListAudits() {
        def audits = [new AuditRevision<SettingsEntry>(revision: new UserRevision(modifiedBy: "a"))]
        def users = [a: new User(userName: "a")]
        when(controller.auditService.findAllByEntity(eq(SettingsEntry), Matchers.any(AuditPagination))).thenReturn(audits)
        when(controller.auditService.collectUsersFromRevisions(audits)).thenReturn(users)
        def mv = controller.list(null)
        assert mv.viewName == "/audit/index"
        assert mv.model.audits == audits
        assert mv.model.users == users
    }


}
