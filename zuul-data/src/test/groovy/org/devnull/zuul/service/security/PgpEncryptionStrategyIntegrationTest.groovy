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
            assert encrypted.startsWith("-----BEGIN PGP MESSAGE")
            assert encrypted.trim().endsWith("-----END PGP MESSAGE-----")
            log.info("Results:\n{}", encrypted)
            log.info("Encryption to {}ms" , time)
        }
    }

    @Test
    void shouldDecryptWithGpgCommandLineIfAvailable() {
        def publicKey = new ClassPathResource("/gpg/acme/acme-public-key.asc").inputStream.text
        def key = new EncryptionKey(algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP, password: publicKey)
        def homedir = new ClassPathResource("/gpg/acme/acme-public-key.asc").file.parent
        def encrypted = new File(homedir, "encrypted.asc.tmp")
        def gpg = "gpg --homedir=${homedir} -d ${encrypted}"

        log.info("Executing {}", gpg)
        encrypted.deleteOnExit()
        encrypted.text = strategy.encrypt("abc", key)
        log.info("Encrypted: {}", encrypted.text)
        try {
            def cmd = gpg.execute()
            def decrypted = cmd.text
            assert decrypted == "abc"
        } catch (IOException e) {
            // This is crappy but I don't want to have everyone install GPG to run tests. At some point, maybe I'll
            // try some sort of dynamic install in the build. I really want the encrypted values decrypted with
            // an external tool.
            if (e.message.contains("The system cannot find the file specified")) {
                log.warn("Unable to run shouldDecryptWithGpgCommandLineIfAvailable because GPG CLI is not installed", e)
            }
            else {
                throw e
            }
        }
    }

}
