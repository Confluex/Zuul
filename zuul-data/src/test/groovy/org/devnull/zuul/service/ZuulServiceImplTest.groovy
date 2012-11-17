package org.devnull.zuul.service

import groovy.mock.interceptor.MockFor
import org.devnull.error.ConflictingOperationException
import org.devnull.error.ValidationException
import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.util.pagination.SimplePagination
import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.specs.SettingsEntryEncryptedWithKey
import org.devnull.zuul.data.specs.SettingsEntrySearch
import org.devnull.zuul.service.security.EncryptionStrategy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Validator
import org.devnull.zuul.data.dao.*

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

public class ZuulServiceImplTest {

    ZuulServiceImpl service
    MetaClass serviceMetaClass

    @Before
    void registerMetaClass() {
        serviceMetaClass = ZuulServiceImpl.metaClass
        def emc = new ExpandoMetaClass(ZuulServiceImpl, true, true)
        emc.initialize()
        GroovySystem.metaClassRegistry.setMetaClass(ZuulServiceImpl, emc)
    }


    @Before
    void createService() {
        def templateMessage = new SimpleMailMessage()
        templateMessage.from = "test@devnull.org"
        service = new ZuulServiceImpl(
                settingsAuditDao: mock(SettingsAuditDao),
                settingsGroupDao: mock(SettingsGroupDao),
                settingsEntryDao: mock(SettingsEntryDao),
                environmentDao: mock(EnvironmentDao),
                encryptionKeyDao: mock(EncryptionKeyDao),
                encryptionStrategy: mock(EncryptionStrategy),
                securityService: mock(SecurityService),
                templateMessage: templateMessage,
                validator: mock(Validator)
        )
    }


    @After
    void resetMetaClass() {
        GroovySystem.metaClassRegistry.setMetaClass(ZuulServiceImpl, serviceMetaClass)
    }

    @Test(expected = ValidationException)
    void shouldThrowValidationExceptionIfBeanIsInvalid() {
        def entry = new SettingsEntry()
        def mockErrors = new MockFor(BeanPropertyBindingResult)
        mockErrors.demand.hasErrors { return true }
        mockErrors.use {
            service.errorIfInvalid(entry, "testEntry")
        }
    }

    @Test
    void shouldCreateEnvironmentWithGivenName() {
        service.createEnvironment("test")
        verify(service.environmentDao).save(new Environment(name: "test"))
    }

    @Test
    void shouldDeleteEnvironmentWithGivenName() {
        service.deleteEnvironment("test")
        verify(service.environmentDao).delete("test")
    }

    @Test
    void shouldEmailSysAdminsWithPermissionsRequests() {
        def requester = new User(email: "user@devnull.org", firstName: "John", lastName: "Doe")
        def sysAdmins = [
                new User(email: "admin1@devnull.org", firstName: "Admin", lastName: "One"),
                new User(email: "admin2@devnull.org", firstName: "Admin", lastName: "Two")
        ]
        def sysAdminRole = new Role(name: "ROLE_SYSTEM_ADMIN", users: sysAdmins)
        def requestedRole = new Role(name: "ROLE_TEST", description: "test role")

        SimpleMailMessage sentMsg = null
        service.mailSender = [
                send: { SimpleMailMessage msg ->  sentMsg = msg }
        ] as MailSender

        when(service.securityService.currentUser).thenReturn(requester)
        when(service.securityService.findRoleByName(ZuulDataConstants.ROLE_SYSTEM_ADMIN)).thenReturn(sysAdminRole)
        when(service.securityService.findRoleByName("ROLE_TEST")).thenReturn(requestedRole)
        service.notifyPermissionsRequest("ROLE_TEST")

        assert sentMsg.to == ['admin1@devnull.org', 'admin2@devnull.org']
        assert sentMsg.cc == ['user@devnull.org']
        assert sentMsg.from == "test@devnull.org"
        assert sentMsg.text == "John Doe has requested access to role: test role"
        assert sentMsg.subject == "Request for permissions: John Doe"
    }

    @Test
    void shouldSaveKey() {
        def key = new EncryptionKey(name: "test", password: "abc")
        when(service.encryptionKeyDao.findOne("test")).thenReturn(key)
        when(service.encryptionKeyDao.save(key)).thenReturn(key)
        def result = service.saveKey(key)
        verify(service.encryptionKeyDao).save(key)
        verify(service.settingsEntryDao, never()).save(Matchers.any(SettingsEntry))
        assert result.is(key)
    }

    @Test
    void shouldReEncryptEntriesWhenSavingKeyWithNewPassword() {
        def existingKey = new EncryptionKey(name: "test", password: "abc")
        def newKey = new EncryptionKey(name: "test", password: "def")

        def reEncryptCount = 0
        service.metaClass.reEncryptEntriesWithMatchingKey = { EncryptionKey a, EncryptionKey b ->
            assert a == existingKey
            assert b == newKey
            reEncryptCount++
        }

        when(service.encryptionKeyDao.findOne("test")).thenReturn(existingKey)
        service.saveKey(newKey)
        assert reEncryptCount == 1
    }

    @Test
    void shouldReEncryptAllMatchingEntriesWithNewKey() {
        def encryptedEntries = [new SettingsEntry(value: "encrypted 1"), new SettingsEntry(value: "encrypted 2")]
        def existingKey = new EncryptionKey(name: "test", password: "abc")
        def newKey = new EncryptionKey(name: "test", password: "def")

        when(service.settingsEntryDao.findAll(new SettingsEntryEncryptedWithKey(existingKey))).thenReturn(encryptedEntries)
        when(service.encryptionStrategy.decrypt("encrypted 1", existingKey)).thenReturn("decrypted 1")
        when(service.encryptionStrategy.decrypt("encrypted 2", existingKey)).thenReturn("decrypted 2")
        service.reEncryptEntriesWithMatchingKey(existingKey, newKey)
        verify(service.settingsEntryDao).findAll(new SettingsEntryEncryptedWithKey(existingKey))
        verify(service.encryptionStrategy).decrypt("encrypted 1", existingKey)
        verify(service.encryptionStrategy).decrypt("encrypted 2", existingKey)
        verify(service.encryptionStrategy).encrypt("decrypted 1", newKey)
        verify(service.encryptionStrategy).encrypt("decrypted 2", newKey)
        verify(service.settingsEntryDao, times(encryptedEntries.size())).save(Matchers.any(SettingsEntry))
    }

    @Test
    void shouldDeleteKeyByName() {
        when(service.encryptionKeyDao.findOne("test")).thenReturn(new EncryptionKey(defaultKey: false))
        service.deleteKey("test")
        verify(service.encryptionKeyDao).delete("test")
    }

    @Test(expected = ConflictingOperationException)
    void shouldThrowExceptionWhenDeletingDefaultKey() {
        when(service.encryptionKeyDao.findOne("test")).thenReturn(new EncryptionKey(defaultKey: true))
        service.deleteKey("test")
    }


    @Test
    void shouldChangeEffectedGroupsToDefaultKeyWhenDeletingKey() {
        def key = new EncryptionKey(name: "a", defaultKey: false)
        def defaultKey = new EncryptionKey(name: "b", defaultKey: true)
        def groups = [new SettingsGroup(id: 1, key: key), new SettingsGroup(id: 2, key: key)]

        when(service.encryptionKeyDao.findAll()).thenReturn([key, defaultKey])
        when(service.encryptionKeyDao.findOne("a")).thenReturn(key)
        when(service.settingsGroupDao.findByKey(key)).thenReturn(groups)
        service.deleteKey("a")
        verify(service.settingsGroupDao).findByKey(key)
        verify(service.encryptionKeyDao).delete("a")
        groups.each {
            assert it.key == defaultKey
        }
    }

    @Test
    void shouldReEncryptEntriesWhenChangingKeys() {
        def oldkey = new EncryptionKey(name: "test old")
        def newKey = new EncryptionKey(name: "test new")
        def group = new SettingsGroup(id: 1, key: oldkey)
        group.addToEntries(new SettingsEntry(key: "a", value: "1"))
        group.addToEntries(new SettingsEntry(key: "b", value: "2", encrypted: true))
        group.addToEntries(new SettingsEntry(key: "c", value: "3"))

        when(service.encryptionStrategy.decrypt("2", oldkey)).thenReturn("2-decrypted")
        service.changeKey(group, newKey)
        verify(service.encryptionStrategy).encrypt("2-decrypted", newKey)
        verify(service.settingsGroupDao).save(group)
        assert group.key == newKey
    }

    @Test
    void shouldFindKeyByName() {
        def expected = new EncryptionKey(name: "test")
        when(service.encryptionKeyDao.findOne("test")).thenReturn(expected)
        def result = service.findKeyByName("test")
        assert result.is(expected)
    }

    @Test
    void shouldChangeDefaultKeyAndSetOldKeyToFalse() {
        def keys = [
                new EncryptionKey(name: "oldKey", defaultKey: true),
                new EncryptionKey(name: "anotherKey", defaultKey: false),
                new EncryptionKey(name: "newKey", defaultKey: false),
        ]

        when(service.encryptionKeyDao.findOne("newKey")).thenReturn(keys[2])
        when(service.encryptionKeyDao.findAll()).thenReturn(keys)
        def updatedKey = service.changeDefaultKey("newKey")
        verify(service.encryptionKeyDao).save([keys[2], keys[0]])

        assert updatedKey.defaultKey
        assert updatedKey == keys[2]
        assert !keys[0].defaultKey
        assert !keys[1].defaultKey
    }

    @Test
    void shouldNotDoAnythingIfTryingToChangeDefaultKeyWhichIsAlreadyTheDefault() {
        def keys = [
                new EncryptionKey(name: "oldKey", defaultKey: true),
                new EncryptionKey(name: "anotherKey", defaultKey: false),
                new EncryptionKey(name: "newKey", defaultKey: false),
        ]

        when(service.encryptionKeyDao.findOne("oldKey")).thenReturn(keys[0])
        def oldKey = service.changeDefaultKey("oldKey")
        verify(service.encryptionKeyDao, never()).save(anyList())
        verify(service.encryptionKeyDao, never()).save(Matchers.any(Iterable))
        assert oldKey == keys[0]
        assert keys[0].defaultKey
        assert !keys[1].defaultKey
        assert !keys[2].defaultKey
    }

    @Test
    void shouldListAllKeysAndSortByName() {
        def expected = [new EncryptionKey(name: "foo")]
        when(service.encryptionKeyDao.findAll(new Sort("name"))).thenReturn(expected)
        def results = service.listEncryptionKeys()
        assert results.is(expected)
    }

    @Test
    void shouldFindCorrectDefaultKey() {
        def mockKeys = [
                new EncryptionKey(name: "a", defaultKey: false),
                new EncryptionKey(name: "b", defaultKey: false),
                new EncryptionKey(name: "c", defaultKey: true),
                new EncryptionKey(name: "d", defaultKey: false)
        ]
        when(service.encryptionKeyDao.findAll()).thenReturn(mockKeys)
        assert service.findDefaultKey().is(mockKeys[2])
    }

    @Test
    void shouldCreateEmptySettingsGroupWithCorrectValues() {
        def mockKeys = [
                new EncryptionKey(name: "a", defaultKey: false),
                new EncryptionKey(name: "b", defaultKey: false),
                new EncryptionKey(name: "c", defaultKey: true),
                new EncryptionKey(name: "d", defaultKey: false)
        ]
        def mockEnvironment = new Environment(name: "testEnv")
        def mockGroup = new SettingsGroup(id: 1, name: "testGroup", environment: mockEnvironment, key: mockKeys[2])


        when(service.environmentDao.findOne(mockEnvironment.name)).thenReturn(mockEnvironment)
        when(service.encryptionKeyDao.findAll()).thenReturn(mockKeys)
        when(service.settingsGroupDao.save(Matchers.any(SettingsGroup))).thenReturn(mockGroup)
        def result = service.createEmptySettingsGroup("testGroup", "testEnv")

        def groupArg = ArgumentCaptor.forClass(SettingsGroup.class)
        verify(service.settingsGroupDao).save(groupArg.capture())
        assert groupArg.value.name == mockGroup.name
        assert groupArg.value.environment == mockGroup.environment
        assert groupArg.value.key == mockGroup.key
        assert !groupArg.value.entries
        assert result.is(mockGroup)
    }

    @Test
    void shouldCopySettingsGroupFromExistingWithCorrectValues() {
        def groupToCopy = new SettingsGroup(
                id: 1, name: "test-config",
                environment: new Environment(name: "prod"),
                key: new EncryptionKey(name: "testKey")
        )
        groupToCopy.addToEntries(new SettingsEntry(key: "username", value: "jdoe"))
        groupToCopy.addToEntries(new SettingsEntry(key: "password", value: "3s+3_23s.zze3if", encrypted: true))
        def environment = new Environment(name: "dev")


        when(service.environmentDao.findOne("dev")).thenReturn(environment)
        service.createSettingsGroupFromCopy("some-config", "dev", groupToCopy)

        def copy = ArgumentCaptor.forClass(SettingsGroup)
        verify(service.settingsGroupDao).save(copy.capture())

        assert copy.value.name == "some-config"
        assert copy.value.environment == environment
        assert copy.value.key == groupToCopy.key

        assert copy.value.entries.size() == 2
        assert !copy.value.entries[0].is(groupToCopy.entries[0])
        assert !copy.value.entries[1].is(groupToCopy.entries[1])

        assert copy.value.entries[0].key == "username"
        assert copy.value.entries[0].value == "jdoe"
        assert !copy.value.entries[0].encrypted

        assert copy.value.entries[1].key == "password"
        assert copy.value.entries[1].value == "3s+3_23s.zze3if"
        assert copy.value.entries[1].encrypted
    }



    @Test
    void findSettingsGroupByNameShouldReturnResultsFromDao() {
        def expected = [new SettingsGroup(name: "some-config")]
        when(service.settingsGroupDao.findByName("some-config")).thenReturn(expected)
        def result = service.findSettingsGroupByName("some-config")
        verify(service.settingsGroupDao).findByName("some-config")
        assert result.is(expected)
    }

    @Test
    void findSettingsGroupByNameAndEnvironmentShouldReturnResultsFromDao() {
        def expected = new SettingsGroup()
        def env = new Environment(name: "prod")
        when(service.settingsGroupDao.findByNameAndEnvironment("some-config", env)).thenReturn(expected)
        def result = service.findSettingsGroupByNameAndEnvironment("some-config", "prod")
        verify(service.settingsGroupDao).findByNameAndEnvironment("some-config", env)
        assert result.is(expected)
    }

    @Test
    void listEnvironmentsShouldReturnResultsFromDao() {
        def expected = [new Environment(name: "a"), new Environment(name: "b")]
        when(service.environmentDao.findAll()).thenReturn(expected)
        def results = service.listEnvironments()
        verify(service.environmentDao).findAll()
        assert results.is(expected)
    }

    @Test
    void listSettingsGroupsShouldSortByName() {
        def expected = [new SettingsGroup(name: "foo")]
        def sort = new Sort("name")
        when(service.settingsGroupDao.findAll(sort)).thenReturn(expected)
        def results = service.listSettingsGroups()
        verify(service.settingsGroupDao).findAll(sort)
        assert results.is(expected)
    }

    @Test(expected = ConflictingOperationException)
    void shouldErrorWhenTryingToEncryptValuesWhichAreAlreadyEncrypted() {
        def entry = new SettingsEntry(id: 1, encrypted: true)
        when(service.settingsEntryDao.findOne(entry.id)).thenReturn(entry)
        service.encryptSettingsEntryValue(entry.id)
    }

    @Test(expected = ConflictingOperationException)
    void shouldErrorWhenTryingToDecryptValuesWhichAreAlreadyDecrypted() {
        def entry = new SettingsEntry(id: 1, encrypted: false)
        when(service.settingsEntryDao.findOne(entry.id)).thenReturn(entry)
        service.decryptSettingsEntryValue(entry.id)
    }

    @Test
    void shouldEncryptSettingsEntryWithItsGroupKey() {
        def group = new SettingsGroup(key: new EncryptionKey(password: "abc123"))
        def entry = new SettingsEntry(id: 1, key: "a", value: "foo")
        group.addToEntries(entry)

        when(service.settingsEntryDao.findOne(entry.id)).thenReturn(entry)
        when(service.settingsEntryDao.save(entry)).thenReturn(entry)
        when(service.encryptionStrategy.encrypt(entry.value, group.key)).thenReturn("encryptedValue")
        def encryptedEntry = service.encryptSettingsEntryValue(entry.id)
        verify(service.encryptionStrategy).encrypt("foo", group.key)
        verify(service.settingsEntryDao).save(entry)
        assert encryptedEntry.encrypted
        assert encryptedEntry.value == "encryptedValue"
    }

    @Test
    void shouldDecryptSettingsEntryWithItsGroupKey() {
        def group = new SettingsGroup(key: new EncryptionKey(password: "abc123"))
        def entry = new SettingsEntry(id: 1, key: "a", value: "encrypted", encrypted: true)
        group.addToEntries(entry)

        when(service.settingsEntryDao.findOne(entry.id)).thenReturn(entry)
        when(service.settingsEntryDao.save(entry)).thenReturn(entry)
        when(service.encryptionStrategy.decrypt(entry.value, group.key)).thenReturn("decrypted")
        def decrypted = service.decryptSettingsEntryValue(entry.id)
        verify(service.encryptionStrategy).decrypt("encrypted", group.key)
        verify(service.settingsEntryDao).save(entry)
        assert !decrypted.encrypted
        assert decrypted.value == "decrypted"
    }

    @Test
    void findEntryShouldReturnResultFromDao() {
        def expected = new SettingsEntry(id: 1)
        when(service.settingsEntryDao.findOne(1)).thenReturn(expected)
        def result = service.findSettingsEntry(1)
        assert result.is(expected)
    }

    @Test
    void deleteSettingsEntryShouldInvokeDao() {
        service.deleteSettingsEntry(1)
        verify(service.settingsEntryDao).delete(1)
    }

    @Test
    void saveSettingsEntryShouldReturnResultsFromDao() {
        def expected = new SettingsEntry(id: 1, key: "a", value: 1)
        when(service.settingsEntryDao.save(expected)).thenReturn(expected)
        def result = service.save(expected)
        verify(service.settingsEntryDao).save(expected)
        assert result.is(expected)
    }

    @Test
    void saveSettingsGroupShouldReturnResultsFromDao() {
        def expected = new SettingsGroup()
        when(service.settingsGroupDao.save(expected)).thenReturn(expected)
        def result = service.save(new SettingsGroup())
        verify(service.settingsGroupDao).save(expected)
        assert result.is(expected)
    }

    @Test
    void deleteSettingsGroupShouldInvokeDao() {
        service.deleteSettingsGroup(1)
        verify(service.settingsGroupDao).delete(1)
    }

    @Test
    void shouldSearchSettingsEntries() {
        def query = new SettingsEntrySearch("abc")
        def page = new PageImpl([new SettingsEntry(id: 1)])
        when(service.settingsEntryDao.findAll(eq(query), any(Pageable))).thenReturn(page)
        def results = service.search("abc", new SimplePagination<SettingsEntry>())
        assert results == page.content
    }

}
