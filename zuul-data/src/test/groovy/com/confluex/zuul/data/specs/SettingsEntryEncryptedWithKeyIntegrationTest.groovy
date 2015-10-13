package com.confluex.zuul.data.specs

import com.confluex.zuul.data.dao.SettingsEntryDao
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsEntryEncryptedWithKeyIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

    @Test
    void shouldFindByEncryptedWithKey() {
        def results = dao.findAll(new SettingsEntryEncryptedWithKey(new EncryptionKey(name: "Default Key")))
        assert results
        results.each {
            assert it.encrypted
            assert it.group.key.name == "Default Key"
        }
    }

    @Test
    void shouldNotFindByEncryptedWithIncorrectKey() {
        def results = dao.findAll(new SettingsEntryEncryptedWithKey(new EncryptionKey(name: "Fake Key")))
        assert !results
    }
}
