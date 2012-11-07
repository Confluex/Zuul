package org.devnull.zuul.web

import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User
import org.devnull.security.model.UserRevision
import org.devnull.security.service.AuditService
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.SettingsEntry
import org.junit.Before
import org.junit.Test

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
        when(controller.auditService.findAllByEntity(SettingsEntry)).thenReturn(audits)
        when(controller.auditService.collectUsersFromRevisions(audits)).thenReturn(users)
        def mv = controller.list()
        assert mv.viewName == "/audit/index"
        assert mv.model.audits == audits
        assert mv.model.users == users
    }


}
