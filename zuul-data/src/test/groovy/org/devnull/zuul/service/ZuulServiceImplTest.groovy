package org.devnull.zuul.service

import org.devnull.zuul.data.dao.SettingsGroupDao
import org.junit.Before
import static org.mockito.Mockito.*
import org.junit.Test
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.dao.EnvironmentDao
import org.springframework.data.domain.Sort
import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.model.EncryptionKey

public class ZuulServiceImplTest {

    ZuulServiceImpl service

    @Before
    void createService() {
        service = new ZuulServiceImpl(
                settingsGroupDao: mock(SettingsGroupDao),
                environmentDao: mock(EnvironmentDao),
                encryptionKeyDao: mock(EncryptionKeyDao)
        )
    }
    
    @Test
    void findSettingsGroupByNameShouldReturnResultsFromDao() {
        def expected = [new SettingsGroup(name:"some-config")]
        when(service.settingsGroupDao.findByName("some-config")).thenReturn(expected)
        def result = service.findSettingsGroupByName("some-config")
        verify(service.settingsGroupDao).findByName("some-config")
        assert result.is(expected)
    }
    
    @Test
    void findSettingsGroupByNameAndEnvironmentShouldReturnResultsFromDao() {
        def expected = new SettingsGroup()
        def env = new Environment(name:"prod")
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
        def expected = [new SettingsGroup(name:"foo")]
        def sort = new Sort("name")
        when(service.settingsGroupDao.findAll(sort)).thenReturn(expected)
        def results = service.listSettingsGroups()
        verify(service.settingsGroupDao).findAll(sort)
        assert results.is(expected)
    }

    @Test
    void shouldEncryptAndDecryptGivenKeyAndValue() {
        when(service.encryptionKeyDao.findOne("Default Key")).thenReturn(new EncryptionKey(password: "k;2(&.sffd919"))

        def encrypted = service.encryptByKeyName("foo", "Default Key")
        println encrypted
        assert encrypted != "foo"
        def decrypted = service.decrypt(encrypted, "Default Key")
        assert decrypted == "foo"

        verify(service.encryptionKeyDao, times(2)).findOne("Default Key")
    }
}
