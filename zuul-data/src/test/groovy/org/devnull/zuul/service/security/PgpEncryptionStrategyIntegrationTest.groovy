package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.test.DataUnitTestMixin
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import java.security.Security

@Mixin(DataUnitTestMixin)
@Slf4j
class PgpEncryptionStrategyIntegrationTest {
    PgpEncryptionStrategy strategy

    @BeforeClass
    static void registerProvider() {
        Security.addProvider(new BouncyCastleProvider())
    }

    @Before
    void createStrategy() {
        strategy = new PgpEncryptionStrategy()
    }

    @Test
    void shouldEncryptDataUsingPublicKey() {
        def publicKey = new ClassPathResource("/gpg/acme/acme-public-key.asc").inputStream.text
        def encrypted = null
        3.times {
            def time = stopWatch {
                def key = new EncryptionKey(algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP, password: publicKey)
                encrypted =  strategy.encrypt("abc", key)
            }
            // pretty crappy assertion but I'm not going to implement PGP decrypt (yet)
            assert encrypted.startsWith("-----BEGIN PGP MESSAGE")
            assert encrypted.trim().endsWith("-----END PGP MESSAGE-----")
            log.info("Results:\n{}", encrypted)
            log.info("Encryption to {}ms" , time)
        }
    }


}
