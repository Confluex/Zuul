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
    void saveSettingsEntryShouldReturnResultsFromDao() {
        def expected = new SettingsEntry(id: 1, key: "a", value: 1)
        when(service.settingsEntryDao.save(expected)).thenReturn(expected)
        def result = service.save(expected)
        verify(service.settingsEntryDao).save(expected)
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
