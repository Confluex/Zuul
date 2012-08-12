package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.error.ConflictingOperationException
import org.junit.Before
import org.junit.Test
import org.springframework.data.domain.Sort

import java.util.concurrent.locks.Lock

import static org.mockito.Mockito.*
import org.mockito.Matchers
import org.mockito.ArgumentCaptor

public class ZuulServiceImplTest {

    ZuulServiceImpl service

    @Before
    void createService() {
        service = new ZuulServiceImpl(
                settingsGroupDao: mock(SettingsGroupDao),
                settingsEntryDao: mock(SettingsEntryDao),
                environmentDao: mock(EnvironmentDao),
                encryptionKeyDao: mock(EncryptionKeyDao)
        )
    }

    @Test
    void shouldFindCorrectDefaultKey() {
        def mockKeys = [
                               new EncryptionKey(name:"a", defaultKey: false),
                               new EncryptionKey(name:"b", defaultKey: false),
                               new EncryptionKey(name:"c", defaultKey: true),
                               new EncryptionKey(name:"d", defaultKey: false)
                       ]
        when(service.encryptionKeyDao.findAll()).thenReturn(mockKeys)
        assert service.findDefaultKey().is(mockKeys[2])
    }

    @Test
    void shouldCreateEmptySettingsGroupWithCorrectValues() {
        def mockKeys = [
                        new EncryptionKey(name:"a", defaultKey: false),
                        new EncryptionKey(name:"b", defaultKey: false),
                        new EncryptionKey(name:"c", defaultKey: true),
                        new EncryptionKey(name:"d", defaultKey: false)
                ]
        def mockEnvironment = new Environment(name: "testEnv")
        def mockGroup = new SettingsGroup(id: 1, name: "testGroup", environment: mockEnvironment, key:  mockKeys[2])


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
                id:1, name: "test-config",
                environment:  new Environment(name: "prod"),
                key: new EncryptionKey(name: "testKey")
        )
        groupToCopy.addToEntries(new SettingsEntry(key: "username", value:"jdoe"))
        groupToCopy.addToEntries(new SettingsEntry(key: "password", value:"3s+3_23s.zze3if", encrypted: true))
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
    void shouldEncryptAndDecryptSettingsEntry() {
        def group = new SettingsGroup(key: new EncryptionKey(password: "abc123"))
        def entries = [new SettingsEntry(id: 1, key: "a", value: "foo", group: group)]

        when(service.settingsEntryDao.findOne(entries[0].id)).thenReturn(entries[0])
        when(service.settingsEntryDao.save(entries[0])).thenReturn(entries[0])

        def encryptedEntry = service.encryptSettingsEntryValue(entries[0].id)
        println encryptedEntry.value
        assert encryptedEntry.encrypted
        assert encryptedEntry.value != "foo"
        verify(service.settingsEntryDao).save(encryptedEntry)

        def decryptedEntry = service.decryptSettingsEntryValue(encryptedEntry.id)
        assert !encryptedEntry.encrypted
        assert decryptedEntry.value == "foo"
        verify(service.settingsEntryDao, times(2)).save(decryptedEntry)
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
    void doWithFlagLockShouldNotAllowConcurrentInvocations() {
        def completed = []
        def threads = []
        threads << Thread.start {
            completed << service.doWithFlagLock {
                sleep(300)
                return 'a'
            }
        }
        threads << Thread.start {
            completed << service.doWithFlagLock {
                return 'b'
            }
        }
        threads*.join()
        assert completed == ['a', 'b']
    }

    @Test(expected=IllegalArgumentException)
    void doWithFlagLockShouldReleaseLockIfExceptionOccurs() {
        service.toggleFlagLock = mock(Lock)
        when(service.toggleFlagLock.tryLock()).thenReturn(true)
        service.doWithFlagLock {
            throw new IllegalArgumentException("test error")
        }
        verify(service.toggleFlagLock).unlock()

    }
}
