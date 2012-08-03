package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.junit.Before
import org.junit.Test
import org.springframework.data.domain.Sort

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

    @Test
    void shouldEncryptAndDecryptSettingsEntry() {
        def group = new SettingsGroup(key: new EncryptionKey(password: "abc123"))
        def entries = [new SettingsEntry(id: 1, key: "a", value: "foo", group: group)]

        when(service.settingsEntryDao.findOne(entries[0].id)).thenReturn(entries[0])
        when(service.settingsEntryDao.save(entries[0])).thenReturn(entries[0])

        def encryptedEntry = service.encryptSettingsEntryValue(entries[0].id)
        println encryptedEntry.value
        assert encryptedEntry.value != "foo"
        verify(service.settingsEntryDao).save(encryptedEntry)

        def decryptedEntry = service.decryptSettingsEntryValue(encryptedEntry.id)
        assert decryptedEntry.value == "foo"
        verify(service.settingsEntryDao, times(2)).save(decryptedEntry)
    }
}
