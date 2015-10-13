package com.confluex.zuul.service.security

import groovy.util.logging.Slf4j
import org.bouncycastle.jce.provider.BouncyCastleProvider
import com.confluex.zuul.data.config.ZuulDataConstants
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.test.DataUnitTestMixin
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
        def encrypted = createEncryptedFile()
        executeGpgIfAvailable("-d ${encrypted}") { response ->
            assert "abc"
        }
    }

    @Test
    void shouldHaveCorrectEncryptedDataPacketLength() {
        def encrypted = createEncryptedFile()
        executeGpgIfAvailable("--list-packets ${encrypted}") { String response ->
            assert response
            def lines = response.split("\n")
            assert lines[4].trim() == "length: 56"
        }
    }

    File createEncryptedFile() {
        def keyResource = new ClassPathResource("/gpg/acme/acme-public-key.asc")
        def publicKey = keyResource.inputStream.text
        def key = new EncryptionKey(algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP, password: publicKey)
        def encrypted = new File(keyResource.file.parent, "encrypted.asc.tmp")
        encrypted.deleteOnExit()
        encrypted.text = strategy.encrypt("abc", key)
        return encrypted
    }

    /*
        This is crappy but I don't want to have everyone install GPG to run tests. At some point, maybe I'll
        try some sort of dynamic install in the build. I really want the encrypted values decrypted with
        an external tool.
     */
    def executeGpgIfAvailable = { gpg, closure ->
        def homedir = new ClassPathResource("/gpg/acme/acme-public-key.asc").file.parent
        gpg = "gpg --homedir ${homedir} ${gpg}"
        log.info("Executing {}", gpg)
        try {
            def cmd = gpg.execute()
            closure(cmd.text)
        } catch (IOException e) {
            def message = e.message
            if (message.contains("cannot find the file") || message.contains("No such file")) {
                log.warn("Unable to run ${gpg} because GPG CLI is not installed", e)
            }
            else {
                throw e
            }
        }
    }
}
