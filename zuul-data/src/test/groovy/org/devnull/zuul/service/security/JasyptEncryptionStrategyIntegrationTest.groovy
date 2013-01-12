package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.DataUnitTestMixin
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource

@Mixin(DataUnitTestMixin)
@Slf4j
class JasyptEncryptionStrategyIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    JasyptEncryptionStrategy strategy

    @Resource(name = 'keyMetaData')
    Map<String, KeyConfiguration> keyMetData

    @Test
    void shouldEncryptAndDecryptTextByKey() {
        assert keyMetData
        keyMetData.each { name, config ->
            def key = new EncryptionKey(password: "test", algorithm: config.algorithm)
            def encrypted = null
            def time = stopWatch { encrypted = strategy.encrypt("abc", key)   }
            log.info("Encryption time: {}ms" , time)
            assert encrypted != "abc"
            log.info("{}: Encrypted: {}", key.algorithm, encrypted)
            def decrypted = strategy.decrypt(encrypted, key)
            assert decrypted == "abc"
        }
    }


}
