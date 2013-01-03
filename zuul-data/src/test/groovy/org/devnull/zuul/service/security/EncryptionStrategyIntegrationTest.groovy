package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.jasypt.exceptions.EncryptionOperationNotPossibleException
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource

@Slf4j
class EncryptionStrategyIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EncryptionStrategy strategy

    @Resource(name='keyMetaData')
    Map<String, KeyConfiguration> keyMetData

    @Test
    void shouldEncryptAndDecryptTextByKey() {
        assert keyMetData
        keyMetData.each { name, config ->
            try {
                def key = new EncryptionKey(password: "test", algorithm: config.algorithm)
                def encrypted = strategy.encrypt("abc", key)
                assert encrypted != "abc"
                log.info("{}: Encrypted: {}", key.algorithm, encrypted)
                def decrypted = strategy.decrypt(encrypted, key)
                assert decrypted == "abc"
            } catch (EncryptionOperationNotPossibleException e) {
                // I hate doing this but Travis CI does not have these installed
                if (e.message.contains("Unlimited Strength Jurisdiction Policy Files")) {
                    log.error("Ignoring test failure because JCE policy files might not be installed", e)
                }
                else {
                    throw(e)
                }
            }
        }
    }


}
