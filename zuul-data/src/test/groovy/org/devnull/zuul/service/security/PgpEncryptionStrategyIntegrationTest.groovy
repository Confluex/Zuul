package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.devnull.zuul.data.model.EncryptionKey
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

@Slf4j
class PgpEncryptionStrategyIntegrationTest {
    PgpEncryptionStrategy strategy

    @Before
    void createStrategy() {
        strategy = new PgpEncryptionStrategy()
    }

    @Test
    void shouldEncryptDataUsingPublicKey() {
        def publicKey = new ClassPathResource("/test-public-key.asc").inputStream.text
        def start = System.currentTimeMillis()
        def encrypted =  strategy.encrypt("abc", new EncryptionKey(password: publicKey))
        def time = System.currentTimeMillis() - start
        assert encrypted.startsWith("-----BEGIN PGP MESSAGE")
        println encrypted
        log.info("Encryption to {}ms" , time)

    }
}
