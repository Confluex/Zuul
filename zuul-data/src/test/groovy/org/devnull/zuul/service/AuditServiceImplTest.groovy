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
        when(service.securityService.currentUser).thenReturn(new User(userName: "userA"))
        def group = new SettingsGroup(
                environment: new Environment(name: "dev"),
                name: "test group"
        )
        group.addToEntries(new SettingsEntry(key: "property.a", value: "1"))
        group.addToEntries(new SettingsEntry(key: "property.b", value: "mumbojumbo", encrypted: true))
        service.logAudit(SettingsAudit.AuditType.ADD, group.entries)
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

}
