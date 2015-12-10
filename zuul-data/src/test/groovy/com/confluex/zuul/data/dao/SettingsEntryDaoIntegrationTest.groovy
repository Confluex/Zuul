package com.confluex.zuul.data.dao

import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test

class SettingsEntryDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

    @Test
    void findOneShouldMapCorrectly() {
        def entry = dao.findOne(1)
        assert entry.id == 1
        assert entry.key == "jdbc.zuul.url"
        assert entry.value == "jdbc:h2:mem:zuul"
        assert entry.group.name == "app-data-config"
        assert entry.group.environment.name == "dev"
        assert entry.group.key.name == "Default Key"
    }
}
