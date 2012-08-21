package org.devnull.zuul.data.specs

import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsEntryEncryptedWithKeyIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

    @Test
    void shouldFindByEncryptedWithKey() {
        def results = dao.findAll(new SettingsEntryEncryptedWithKey(new EncryptionKey(name: "Default Key")))
        // TODO create test-case/support issue for spring-jpa. Examining the SQL shows duplicate queries
        assert results
        results.each {
            assert it.encrypted
            assert it.group.key.name == "Default Key"
        }
    }
}
