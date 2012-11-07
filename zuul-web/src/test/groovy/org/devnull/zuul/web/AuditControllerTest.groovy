package org.devnull.zuul.web

import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User
import org.devnull.security.model.UserRevision
import org.devnull.security.service.AuditService
import org.devnull.zuul.data.model.SettingsEntry
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*
import org.devnull.security.service.SecurityService

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController(auditService: mock(AuditService), securityService: mock(SecurityService))
    }

    @Test
    void shouldListAudits() {
        def audits = [new AuditRevision<SettingsEntry>(revision: new UserRevision(modifiedBy: "a"))]
        def user = new User(userName: "a")
        when(controller.auditService.findAllByEntity(SettingsEntry)).thenReturn(audits)
        when(controller.securityService.findByUserName("a")).thenReturn(user)
        def mv = controller.list()
        assert mv.viewName == "/audit/index"
        assert mv.model.audits == audits
        assert mv.model.users == ["a":user]
    }

    @Test
    void shouldFindDistinctListOfUsersWhoCreatedRevisions() {
        def audits = [
                new AuditRevision(revision: new UserRevision(modifiedBy: "userA")),
                new AuditRevision(revision: new UserRevision(modifiedBy: "userB")),
                new AuditRevision(revision: new UserRevision(modifiedBy: "userB")),
                new AuditRevision(revision: new UserRevision(modifiedBy: "userA")),
                new AuditRevision(revision: new UserRevision(modifiedBy: "userB")),
        ]
        def userA = new User(userName: "userA")
        when(controller.securityService.findByUserName("userA")).thenReturn(userA)
        def userB = new User(userName: "userB")
        when(controller.securityService.findByUserName("userB")).thenReturn(userB)
        def users = controller.collectUsersFromRevisions(audits)
        assert users == [ "userA": userA, "userB": userB  ]
    }
}
