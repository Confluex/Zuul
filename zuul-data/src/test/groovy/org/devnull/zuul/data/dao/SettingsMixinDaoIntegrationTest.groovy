package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsMixinDaoIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    SettingsMixinDao settingsMixinDao

    @Test
    void shouldFindByIdAndMapCorrectly() {
        def mixin = settingsMixinDao.findOne(1)
        assert mixin.id == 1
        assert mixin.parent == "hr-service-config"
        assert mixin.target == "app-data-config"
        assert mixin.ordinal == 1
    }
}
