package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test

class SettingsEntryDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

    @Test
    void findOneShouldMapCorrectly() {
        def entry = dao.findOne(1)
        assert entry.id == 1
    }
}
