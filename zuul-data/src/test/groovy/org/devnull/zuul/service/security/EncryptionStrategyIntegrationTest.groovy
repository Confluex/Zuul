package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource

@Slf4j
class EncryptionStrategyIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EncryptionStrategy strategy

    @Resource
    List<KeyConfiguration> keyConfigurations

    @Test
    void shouldEncryptAndDecryptTextByKey() {
        assert keyConfigurations
        keyConfigurations.each { config ->
            def key = new EncryptionKey(password: "test", algorithm: config.algorithm)
            def encrypted = strategy.encrypt("abc", key)
            assert encrypted != "abc"
            log.info("{}: Encrypted: {}", key.algorithm, encrypted)
            def decrypted = strategy.decrypt(encrypted, key)
            assert decrypted == "abc"
        }
    }


}
