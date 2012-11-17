package org.devnull.zuul.service

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.mockito.Mockito.*

class AuditServiceImplTest {
    AuditServiceImpl service

    @Before
    void createService() {
        service = new AuditServiceImpl(
                settingsAuditDao: mock(SettingsAuditDao),
                securityService: mock(SecurityService)
        )
    }

    @Test
    void shouldSaveAuditSettingsEntries() {
        def group = new SettingsGroup(
                environment: new Environment(name: "dev"),
                name: "test group"
        )
        group.addToEntries(new SettingsEntry(key: "property.a", value: "1"))
        group.addToEntries(new SettingsEntry(key: "property.b", value: "mumbojumbo", encrypted: true))
        service.logAudit(new User(userName: "userA"), SettingsAudit.AuditType.ADD, group.entries)
        def args = ArgumentCaptor.forClass(SettingsAudit)
        verify(service.settingsAuditDao, times(2)).save(args.capture())
        // not sure how to get a captor for each invocation.. just use the last for now
        assert args.value.encrypted
        assert args.value.groupEnvironment == "dev"
        assert args.value.groupName == "test group"
        assert args.value.settingsKey == "property.b"
        assert args.value.settingsValue == "mumbojumbo"
        assert args.value.modifiedBy == "userA"
        assert args.value.type == SettingsAudit.AuditType.ADD
    }

    @Test
    void shouldFindUsersFromAuditUserNames() {
        def audits = [
                new SettingsAudit(modifiedBy: "userA"),
                new SettingsAudit(modifiedBy: "userB"),
                new SettingsAudit(modifiedBy: "userA"),
                new SettingsAudit(modifiedBy: "userC"),
                new SettingsAudit(modifiedBy: "userB"),
        ]
        def userA = new User(userName: "userA")
        def userB = new User(userName: "userB")
        def userC = new User(userName: "userC")

        when(service.securityService.findByUserName("userA")).thenReturn(userA)
        when(service.securityService.findByUserName("userB")).thenReturn(userB)
        when(service.securityService.findByUserName("userC")).thenReturn(userC)

        def users = service.lookupUsersForAudits(audits)

        // ensure only 1 call per user
        verify(service.securityService).findByUserName("userA")
        verify(service.securityService).findByUserName("userB")
        verify(service.securityService).findByUserName("userC")

        assert users.size() == 3
        assert users["userA"] == userA
        assert users["userB"] == userB
        assert users["userC"] == userC

    }

    @Test
    void shouldCreateDummyUserIfRecordNoLongerExists() {
        def audits = [
                new SettingsAudit(modifiedBy: "userA"),
                new SettingsAudit(modifiedBy: "userB")
        ]
        def userA = new User(userName: "userA")

        when(service.securityService.findByUserName("userA")).thenReturn(userA)

        def users = service.lookupUsersForAudits(audits)

        // ensure only 1 call per user
        verify(service.securityService).findByUserName("userA")
        verify(service.securityService).findByUserName("userB")

        assert users.size() == 2
        assert users["userA"] == userA
        assert users["userB"].userName == "userB"
        assert users["userB"].firstName == "Deleted"
        assert users["userB"].lastName == "User"
    }
}
