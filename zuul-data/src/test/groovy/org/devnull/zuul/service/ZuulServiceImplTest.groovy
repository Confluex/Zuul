package org.devnull.zuul.service

import org.devnull.zuul.data.dao.SettingsGroupDao
import org.junit.Before
import static org.mockito.Mockito.*
import org.junit.Test
import org.devnull.zuul.data.model.SettingsGroup

public class ZuulServiceImplTest {

    ZuulServiceImpl service

    @Before
    void createService() {
        service = new ZuulServiceImpl(settingsGroupDao: mock(SettingsGroupDao))
    }
    
    @Test
    void findSettingsGroupByNameAndEnvironmentShouldReturnResultsFromDao() {
        def expected = new SettingsGroup()
        when(service.settingsGroupDao.findByNameAndEnvironment("some-config", "prod")).thenReturn(expected)
        def result = service.findSettingsGroupByNameAndEnvironment("some-config", "prod")
        verify(service.settingsGroupDao).findByNameAndEnvironment("some-config", "prod")
        assert result.is(expected)
    }
}
