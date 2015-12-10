package com.confluex.zuul.service

import groovy.mock.interceptor.MockFor
import com.confluex.error.ConflictingOperationException
import com.confluex.error.ValidationException
import com.confluex.security.model.Role
import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import com.confluex.util.pagination.SimplePagination
import com.confluex.zuul.data.config.ZuulDataConstants
import com.confluex.zuul.data.dao.EncryptionKeyDao
import com.confluex.zuul.data.dao.EnvironmentDao
import com.confluex.zuul.data.dao.SettingsDao
import com.confluex.zuul.data.dao.SettingsEntryDao
import com.confluex.zuul.data.dao.SettingsGroupDao
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.model.Settings
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup
import com.confluex.zuul.data.specs.SettingsEntryEncryptedWithKey
import com.confluex.zuul.data.specs.SettingsEntrySearch
import com.confluex.zuul.service.error.InvalidOperationException
import com.confluex.zuul.service.security.EncryptionStrategy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Validator

import java.util.concurrent.locks.Lock

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
        templateMessage.from = "test@confluex.org"
        service = new ZuulServiceImpl(
                settingsLock: mock(Lock),
                settingsDao: mock(SettingsDao),
                settingsGroupDao: mock(SettingsGroupDao),
                settingsEntryDao: mock(SettingsEntryDao),
                environmentDao: mock(EnvironmentDao),
                encryptionKeyDao: mock(EncryptionKeyDao),
                encryptionStrategy: mock(EncryptionStrategy),
                auditService: mock(AuditService),
                securityService: mock(SecurityService),
                templateMessage: templateMessage,
                validator: mock(Validator)
        )
    }


    @After
    void resetMetaClass() {
        GroovySystem.metaClassRegistry.setMetaClass(ZuulServiceImpl, serviceMetaClass)
    }

    @Test
    void shouldDeleteSettings() {
        def settings = new Settings(id: 1, name: "done for")
        service.delete(settings)
        verify(service.settingsDao).delete(settings.id)
    }

    @Test
    void shouldSaveSettings() {
        def settings = new Settings(id: 1, name: "test")
        when(service.settingsDao.save(settings)).thenReturn(settings)
        def results = service.save(settings)
        verify(service.settingsDao).save(settings)
        assert results.is(settings)
    }

    @Test(expected = ValidationException)
    void shouldErrorWhenSavingInvalidSettings() {
        def settings = new Settings(id: 1, name: "test")
        def mockErrors = new MockFor(BeanPropertyBindingResult)
        mockErrors.demand.hasErrors { return true }
        mockErrors.use {
            service.save(settings)
        }
    }

    @Test
    void shouldGetSettingsRecordByName() {
        def settings = new Settings(name: "test")
        when(service.settingsDao.findByName(settings.name)).thenReturn(settings)
        def results = service.getSettingsByName("test")
        assert results == settings
    }

    @Test
    void shouldReturnNullWhenGettingSettingsByNameAndItDoesNotExist() {
        def results = service.getSettingsByName("test")
        assert results == null
    }

    @Test
    void shouldCreateNewSettingsIfTheyDoNotExist() {
        def settings = new Settings(name: "test-settings")
        when(service.settingsDao.findByName(settings.name)).thenReturn(null)
        when(service.settingsDao.save(eq(settings))).thenReturn(settings)
        def results = service.findOrCreateSettingsByName(settings.name)
        verify(service.settingsDao).save(eq(settings))
        verify(service.settingsLock).lock()
        verify(service.settingsLock).unlock()
        assert results == settings
    }

    @Test
    void shouldUseExistingSettingsIfItExists() {
        def settings = new Settings(name: "test-settings")
        when(service.settingsDao.findByName(settings.name)).thenReturn(settings)
        def results = service.findOrCreateSettingsByName(settings.name)
        verify(service.settingsDao, never()).save(any(Settings))
        verify(service.settingsLock).lock()
        verify(service.settingsLock).unlock()
        assert results == settings
    }

    @Test
    void shouldReleaseLockIfErrorOccursWhileFindingOrCreatingSettings() {
        def settings = new Settings(name: "test-settings")
        when(service.settingsDao.findByName(settings.name)).thenThrow(new RuntimeException("test"))
        def thrown = assertException(RuntimeException) {
            service.findOrCreateSettingsByName(settings.name)
        }
        assert thrown.message == "test"
        verify(service.settingsDao, never()).save(any(Settings))
        verify(service.settingsLock).lock()
        verify(service.settingsLock).unlock()
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
        def requester = new User(email: "user@confluex.org", firstName: "John", lastName: "Doe")
        def sysAdmins = [
                new User(email: "admin1@confluex.org", firstName: "Admin", lastName: "One"),
                new User(email: "admin2@confluex.org", firstName: "Admin", lastName: "Two")
        ]
        def sysAdminRole = new Role(name: "ROLE_SYSTEM_ADMIN", users: sysAdmins)
        def requestedRole = new Role(name: "ROLE_TEST", description: "test role")

        SimpleMailMessage sentMsg = null
        service.mailSender = [
                send: { SimpleMailMessage msg -> sentMsg = msg }
        ] as MailSender

        when(service.securityService.currentUser).thenReturn(requester)
        when(service.securityService.findRoleByName(ZuulDataConstants.ROLE_SYSTEM_ADMIN)).thenReturn(sysAdminRole)
        when(service.securityService.findRoleByName("ROLE_TEST")).thenReturn(requestedRole)
        service.notifyPermissionsRequest("ROLE_TEST")

        assert sentMsg.to == ['admin1@confluex.org', 'admin2@confluex.org']
        assert sentMsg.cc == ['user@confluex.org']
        assert sentMsg.from == "test@confluex.org"
        assert sentMsg.text == "John Doe has requested access to role: test role"
        assert sentMsg.subject == "Request for permissions: John Doe"
    }

    @Test
    void shouldCreateNewKeys() {
        def key = new EncryptionKey(name: "test", password: "abc")
        when(service.encryptionKeyDao.save(key)).thenReturn(key)
        def result = service.saveKey(key)
        verify(service.encryptionKeyDao).findOne("test")
        verify(service.encryptionKeyDao).save(key)
        verify(service.settingsEntryDao, never()).save(Matchers.any(SettingsEntry))
        assert result.is(key)
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

    @Test(expected = InvalidOperationException)
    void shouldThrowExceptionWhenDeletingDefaultKey() {
        when(service.encryptionKeyDao.findOne("test")).thenReturn(new EncryptionKey(defaultKey: true))
        service.deleteKey("test")
    }

    @Test(expected = InvalidOperationException)
    void shouldThrowExceptionWhenDeletingPgpKeysAssignedToSettingsWithEncryptedValues() {
        def key = new EncryptionKey(name: "test", algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP)
        def entries = [new SettingsEntry(encrypted: false), new SettingsEntry(encrypted: true)]
        def group = new SettingsGroup(key: key, entries: entries)
        when(service.encryptionKeyDao.findOne("test")).thenReturn(key)
        when(service.settingsGroupDao.findByKey(key)).thenReturn([group])
        service.deleteKey("test")
    }

    @Test
    void shouldDeletePgpKeysAssignedSettingsWithNoEncryptedValues() {
        def key = new EncryptionKey(name: "test", algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP)
        def entries = [new SettingsEntry(encrypted: false), new SettingsEntry(encrypted: false)]
        def group = new SettingsGroup(key: key, entries: entries)
        when(service.encryptionKeyDao.findOne("test")).thenReturn(key)
        when(service.settingsGroupDao.findByKey(key)).thenReturn([group])
        service.deleteKey("test")
        verify(service.encryptionKeyDao).delete(key.name)
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
        def mockSettings = new Settings(name: "testGroup")
        def mockEnvironment = new Environment(name: "testEnv")
        def mockGroup = new SettingsGroup(id: 1, environment: mockEnvironment, key: mockKeys[2])


        when(service.environmentDao.findOne(mockEnvironment.name)).thenReturn(mockEnvironment)
        when(service.encryptionKeyDao.findAll()).thenReturn(mockKeys)
        when(service.settingsDao.findByName("testGroup")).thenReturn(mockSettings)
        when(service.settingsGroupDao.save(Matchers.any(SettingsGroup))).thenReturn(mockGroup)
        def result = service.createEmptySettingsGroup("testGroup", "testEnv")

        def groupArg = ArgumentCaptor.forClass(SettingsGroup.class)
        verify(service.settingsGroupDao).save(groupArg.capture())
        assert groupArg.value.settings.name == mockSettings.name
        assert groupArg.value.environment == mockGroup.environment
        assert groupArg.value.key == mockGroup.key
        assert !groupArg.value.entries
        assert result.is(mockGroup)
    }

    @Test
    void shouldCreateSettingsGroupFromPropertiesFile() {
        def environment = new Environment(name: "qa")
        def settings = new Settings(name: "test-data-config").addToGroups(new SettingsGroup(environment: environment))
        def group = settings.groups.first()
        def stream = new ClassPathResource("/test-data-config-qa.properties")
        def user = new User(userName: "userA")

        when(service.settingsDao.findByName(settings.name)).thenReturn(settings)
        when(service.settingsGroupDao.save(Matchers.any(SettingsGroup))).thenReturn(group)
        when(service.environmentDao.findOne(environment.name)).thenReturn(environment)
        when(service.securityService.currentUser).thenReturn(user)
        def result = service.createSettingsGroupFromPropertiesFile("test-data-config", "qa", stream.inputStream)

        def arg = ArgumentCaptor.forClass(SettingsGroup)
        verify(service.settingsGroupDao, times(2)).save(arg.capture())

        assert arg.value == result

        assert result.settings == settings
        assert result.environment == environment
        assert result.entries.size() == 2

        assert result.entries[0].key == "jdbc.zuul.generate.ddl"
        assert result.entries[0].value == "validate"
        assert !result.entries[0].encrypted

        assert result.entries[1].key == "jdbc.zuul.url"
        assert result.entries[1].value == "jdbc:h2:file:zuul-qa"
        assert !result.entries[1].encrypted
    }

    @Test
    void shouldCopySettingsGroupFromExistingWithCorrectValues() {
        def group = new SettingsGroup(
                id: 1, environment: new Environment(name: "prod"),
                key: new EncryptionKey(name: "testKey")
        )
        group.addToEntries(new SettingsEntry(key: "username", value: "jdoe"))
        group.addToEntries(new SettingsEntry(key: "password", value: "3s+3_23s.zze3if", encrypted: true))
        def settings = new Settings(name: "some-config").addToGroups(group)
        def newEnvironment = new Environment(name: "dev")

        when(service.settingsDao.findByName(settings.name)).thenReturn(settings)
        when(service.environmentDao.findOne(newEnvironment.name)).thenReturn(newEnvironment)
        service.createSettingsGroupFromCopy("some-config", "dev", group)

        def copy = ArgumentCaptor.forClass(SettingsGroup)
        verify(service.settingsGroupDao).save(copy.capture())

        assert copy.value.settings.name == "some-config"
        assert copy.value.environment == newEnvironment
        assert copy.value.key == group.key

        assert copy.value.entries.size() == 2
        assert !copy.value.entries[0].is(group.entries[0])
        assert !copy.value.entries[1].is(group.entries[1])

        assert copy.value.entries[0].key == "username"
        assert copy.value.entries[0].value == "jdoe"
        assert !copy.value.entries[0].encrypted

        assert copy.value.entries[1].key == "password"
        assert copy.value.entries[1].value == "3s+3_23s.zze3if"
        assert copy.value.entries[1].encrypted
    }



    @Test
    void findSettingsGroupByNameShouldReturnResultsFromDao() {
        def settings = new Settings(
                id: 100,
                name: "some-config",
                groups: [new SettingsGroup(id: 1), new SettingsGroup(id: 2)]
        )
        when(service.settingsDao.findByName("some-config")).thenReturn(settings)
        def result = service.findSettingsGroupByName("some-config")
        verify(service.settingsDao).findByName("some-config")
        assert result == settings.groups
    }

    @Test
    void findSettingsGroupByNameAndEnvironmentShouldReturnResultsFromDao() {
        def group = new SettingsGroup(id: 1)
        def env = new Environment(name: "prod")
        def settings = new Settings(id: 100)
        when(service.settingsDao.findByName("some-config")).thenReturn(settings)
        when(service.settingsGroupDao.findBySettingsAndEnvironment(settings, env)).thenReturn(group)
        def result = service.findSettingsGroupByNameAndEnvironment("some-config", "prod")
        verify(service.settingsDao).findByName("some-config")
        verify(service.settingsGroupDao).findBySettingsAndEnvironment(settings, env)
        assert result.is(group)
    }

    @Test
    void shouldListEnvironmentsSortedByOrdinalAndName() {
        def expected = [new Environment(name: "a"), new Environment(name: "b")]
        def sort = new Sort("ordinal", "name")
        when(service.environmentDao.findAll(sort)).thenReturn(expected)
        def results = service.listEnvironments()
        verify(service.environmentDao).findAll(sort)
        assert results.is(expected)
    }

    @Test
    void shouldPersistSortedEnvironmentsWithProperOrdinal() {
        def names = ["a", "d", "b"]
        def environments = [
                new Environment(name: "z"), // shouldn't update this one
                new Environment(name: "d"),
                new Environment(name: "a"),
                new Environment(name: "b")
        ]
        when(service.environmentDao.findAll()).thenReturn(environments)
        service.sortEnvironments(names)
        verify(service.environmentDao).save(environments)

        assert environments[0].name == "z" && environments[0].ordinal == 0
        assert environments[1].name == "d" && environments[1].ordinal == 1
        assert environments[2].name == "a" && environments[2].ordinal == 0
        assert environments[3].name == "b" && environments[3].ordinal == 2
    }

    @Test
    void listSettingsShouldSortByName() {
        def expected = [new Settings(name: "foo")]
        def sort = new Sort("name")
        when(service.settingsDao.findAll(sort)).thenReturn(expected)
        def results = service.listSettings()
        verify(service.settingsDao).findAll(sort)
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
    void shouldDeleteSettingsEntry() {
        def entry = new SettingsEntry(id: 1)
        service.deleteSettingsEntry(entry)
        verify(service.settingsEntryDao).delete(1)
    }

    @Test
    void shouldCreateEntriesForGroup() {
        def group = new SettingsGroup()
        def entry = new SettingsEntry()
        service.createEntry(group, entry)
        def arg = ArgumentCaptor.forClass(SettingsEntry)
        verify(service.encryptionStrategy, never()).encrypt(anyString(), any(EncryptionKey))
        verify(service.settingsEntryDao).save(arg.capture())
        assert arg.value.group == group
        assert group.entries.contains(arg.value)
    }

    @Test
    void shouldEncryptWhileCreatingNewEntriesIfEntityHasEncryptedFlag() {
        def group = new SettingsGroup(key: new EncryptionKey())
        def entry = new SettingsEntry(value: "test", encrypted: true)
        when(service.encryptionStrategy.encrypt("test", group.key)).thenReturn("mumbojumbo")
        service.createEntry(group, entry)
        verify(service.encryptionStrategy).encrypt("test", group.key)
        verify(service.settingsEntryDao).save(entry)
        assert entry.group == group
        assert entry.value == "mumbojumbo"
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
        service.deleteSettingsGroup(new SettingsGroup(id: 1))
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

    @Test
    void shouldLogAuditWhenSavingGroup() {
        def group = new SettingsGroup(id: 1)
        def user = new User(userName: "testUser")
        when(service.securityService.currentUser).thenReturn(user)
        service.save(group)
        verify(service.auditService).logAudit(user, group)
    }

    @Test
    void shouldLogAuditAsModifyWhenSavingExistingEntry() {
        def entry = new SettingsEntry(id: 1)
        def user = new User(userName: "testUser")
        when(service.securityService.currentUser).thenReturn(user)
        service.save(entry)
        verify(service.auditService).logAudit(user, entry, SettingsAudit.AuditType.MOD)
    }

    @Test
    void shouldLogAuditAsModifyWhenSavingNewEntry() {
        def entry = new SettingsEntry(id: null)
        def user = new User(userName: "testUser")
        when(service.securityService.currentUser).thenReturn(user)
        service.save(entry)
        verify(service.auditService).logAudit(user, entry, SettingsAudit.AuditType.ADD)
    }

    @Test
    void shouldLogAuditWhenDeletingEntry() {
        def user = new User(userName: "testUser")
        def entry = new SettingsEntry(id: 1)
        when(service.securityService.currentUser).thenReturn(user)
        service.deleteSettingsEntry(entry)
        verify(service.auditService).logAudit(user, entry, SettingsAudit.AuditType.DELETE)
    }

    @Test
    void shouldLogAuditWhenDeletingGroup() {
        def user = new User(userName: "testUser")
        when(service.securityService.currentUser).thenReturn(user)
        def group = new SettingsGroup(id: 1)
        service.deleteSettingsGroup(group)
        verify(service.auditService).logAudit(user, group, SettingsAudit.AuditType.DELETE)
    }

    @Test
    void shouldToggleEnvironmentRestrictionFlagToFalseIfAlreadyTrue() {
        def environment = new Environment(name: "testEnv", restricted: true)
        when(service.environmentDao.findOne("testEnv")).thenReturn(environment)
        def result = service.toggleEnvironmentRestriction("testEnv")
        def arg = ArgumentCaptor.forClass(Environment)
        verify(service.environmentDao).save(arg.capture())
        assert !arg.value.restricted
        assert result == arg.value.restricted
    }

    @Test
    void shouldToggleEnvironmentRestrictionFlagToTrueIfAlreadyFalse() {
        def environment = new Environment(name: "testEnv", restricted: false)
        when(service.environmentDao.findOne("testEnv")).thenReturn(environment)
        def result = service.toggleEnvironmentRestriction("testEnv")
        def arg = ArgumentCaptor.forClass(Environment)
        verify(service.environmentDao).save(arg.capture())
        assert arg.value.restricted
        assert result == arg.value.restricted
    }

    protected def assertException = { Class expected, closure ->
        Throwable thrown = null
        try {
            closure()
        }
        catch (Throwable e) {
            thrown = e
        }
        assert thrown
        assert thrown.class == expected
        return thrown
    }
}
