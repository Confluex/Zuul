package org.devnull.zuul.service.security

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EncryptionStrategyIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EncryptionStrategy strategy

    @Test
    void shouldEncryptAndDecryptTextByKey() {
        def key = new EncryptionKey(password: "test")
        def encrypted = strategy.encrypt("abc", key)
        assert encrypted != "abc"
        def decrypted = strategy.decrypt(encrypted, key)
        assert decrypted == "abc"
    }


}
