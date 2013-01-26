package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsDao dao

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Test
    void shouldFindSettingsAndMapCorrectly() {
        def settings = dao.findAll()
        assert settings.size() == 2
        assert settings[0].name == "app-data-config"
        assert settings[1].name == "hr-service-config"
    }

    @Test
    void shouldFindSettingsByName() {
        def settings = dao.findByName("hr-service-config")
        assert settings.name == "hr-service-config"
    }
}
