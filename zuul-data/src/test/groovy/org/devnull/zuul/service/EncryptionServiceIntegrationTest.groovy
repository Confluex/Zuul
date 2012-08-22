package org.devnull.zuul.service

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EncryptionServiceIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EncryptionService service

    @Test
    void shouldEncryptAndDecryptTextByKey() {
        def key = new EncryptionKey(password: "test")
        def encrypted = service.encrypt("abc", key)
        assert encrypted != "abc"
        def decrypted = service.decrypt(encrypted, key)
        assert decrypted == "abc"
    }


}
