package org.devnull.zuul.service

import org.devnull.zuul.data.dao.SettingsGroupDao
import org.junit.Before
import static org.mockito.Mockito.*
import org.junit.Test
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.model.Environment

public class ZuulServiceImplTest {

    ZuulServiceImpl service

    @Before
    void createService() {
        service = new ZuulServiceImpl(settingsGroupDao: mock(SettingsGroupDao))
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
}
