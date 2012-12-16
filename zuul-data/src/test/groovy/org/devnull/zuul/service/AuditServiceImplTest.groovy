package org.devnull.zuul.service

import org.devnull.orm.util.JpaPaginationAdapter
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.util.pagination.SimplePagination
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.specs.SettingsAuditFilter
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

class AuditServiceImplTest {

    AuditServiceImpl service

    @Before
    void createService() {
        service = new AuditServiceImpl(
                settingsAuditDao: mock(SettingsAuditDao),
                settingsEntryDao: mock(SettingsEntryDao),
                settingsGroupDao: mock(SettingsGroupDao),
                securityService: mock(SecurityService)
        )
    }

    @Test
    void shouldFindAuditsFromPagination() {
        def audits = [new SettingsAudit(id: 1)]
        def filter = [id: 1]
        def pagination = new SimplePagination<SettingsAudit>(filter: filter)
        def page = mock(Page)
        when(service.settingsAuditDao.findAll(any(Specification), any(Pageable))).thenReturn(page)
        when(page.content).thenReturn(audits)

        def results = service.findSettingAudits(pagination)
        def specArg = ArgumentCaptor.forClass(SettingsAuditFilter)
        def pagingArg = ArgumentCaptor.forClass(JpaPaginationAdapter)
        verify(service.settingsAuditDao).findAll(specArg.capture(), pagingArg.capture())
        assert specArg.value.filter == filter
        assert pagingArg.value.pageNumber == pagination.page
        assert pagingArg.value.pageSize == pagination.max
        assert results == audits
    }


    @Test
    void shouldSaveAuditSettingsEntriesByGroup() {
        def group = createGroup()
        service.logAudit(new User(userName: "userA"), group, SettingsAudit.AuditType.ADD)
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
    void shouldSaveAuditSettingsByEntry() {
        def group = createGroup()
        service.logAudit(new User(userName: "userA"), group.entries.first())
        def args = ArgumentCaptor.forClass(SettingsAudit)
        verify(service.settingsAuditDao).save(args.capture())
        assert !args.value.encrypted
        assert args.value.groupEnvironment == "dev"
        assert args.value.groupName == "test group"
        assert args.value.settingsKey == "property.a"
        assert args.value.settingsValue == "1"
        assert args.value.modifiedBy == "userA"
        assert args.value.type == SettingsAudit.AuditType.ADD
    }

    @Test
    void shouldNotThrowExceptionIfErrorOccursSavingAudit() {
        when(service.settingsAuditDao.save(any(SettingsAudit))).thenThrow(new RuntimeException("test"))
        service.logAudit(new User(userName: "userA"), new SettingsEntry())
        verify(service.settingsAuditDao).save(any(SettingsAudit))    }


    @Test
    void shouldSaveAuditSettingsByEntryWithModifiedTypeIfIdPropertyIsNotNull() {
        def entry = createGroup().entries.first()
        entry.id = 1
        service.logAudit(new User(userName: "userA"), entry)
        def args = ArgumentCaptor.forClass(SettingsAudit)
        verify(service.settingsAuditDao).save(args.capture())
        assert args.value.type == SettingsAudit.AuditType.MOD
    }

    @Test
    void shouldSaveAuditSettingsWhenDeletingEntryById() {
        def group = createGroup()
        when(service.settingsEntryDao.findOne(1)).thenReturn(group.entries.first())
        service.logAudit(new User(userName: "userA"), group.entries.first(), SettingsAudit.AuditType.DELETE)
        def args = ArgumentCaptor.forClass(SettingsAudit)
        verify(service.settingsAuditDao).save(args.capture())
        assert !args.value.encrypted
        assert args.value.groupEnvironment == "dev"
        assert args.value.groupName == "test group"
        assert args.value.settingsKey == "property.a"
        assert args.value.settingsValue == "1"
        assert args.value.modifiedBy == "userA"
        assert args.value.type == SettingsAudit.AuditType.DELETE
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
        assert users["userB"].email == "deleted@devnull.org"
    }

    protected SettingsGroup createGroup() {
        def group = new SettingsGroup(
                id: 22,
                environment: new Environment(name: "dev"),
                name: "test group"
        )
        group.addToEntries(new SettingsEntry(key: "property.a", value: "1"))
        group.addToEntries(new SettingsEntry(key: "property.b", value: "mumbojumbo", encrypted: true))
        when(service.settingsGroupDao.findOne(22)).thenReturn(group)
        return group
    }

}
