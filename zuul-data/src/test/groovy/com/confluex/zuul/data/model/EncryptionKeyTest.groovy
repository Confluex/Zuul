package com.confluex.zuul.data.model

import org.bouncycastle.openpgp.PGPPublicKey
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import com.confluex.zuul.data.config.ZuulDataConstants
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class EncryptionKeyTest {
    EncryptionKey key

    @Before
    void createKey() {
        key = new EncryptionKey(name: "foo", password: "secret", algorithm: "PBE-ABC")
    }

    @Test
    void toStringShouldNotContainPassword() {
        assert !key.toString().contains("secret")
    }

    @Test
    void shouldNotBeCompatibleIfPasswordChanges() {
        def newKey = new EncryptionKey(name: key.name, algorithm: key.algorithm, password: "new password")
        assert !key.compatibleWith(newKey)
    }

    @Test
    void shouldNotBeCompatibleIfAlgorithmChanges() {
        def newKey = new EncryptionKey(name: key.name, algorithm: "PBE-DEF", password: key.password)
        assert !key.compatibleWith(newKey)
    }

    @Test
    void shouldNotBeCompatibleIfAlgorithmAndPasswordChanges() {
        def newKey = new EncryptionKey(name: key.name, algorithm: "PBE-DEF", password: "new password")
        assert !key.compatibleWith(newKey)
    }

    @Test
    void shouldBeCompatibleIfNameChangesButAlgorithmAndPasswordAreTheSame() {
        def newKey = new EncryptionKey(name: "new key", algorithm: key.algorithm, password: key.password)
        assert key.compatibleWith(newKey)
    }

    @Test
    void shouldKnowIfItIsAPgpKey() {
        ZuulDataConstants.PGP_KEY_ALGORITHMS.each {
            def key = new EncryptionKey(algorithm: it)
            assert key.isPgpKey
            assert !key.isPbeKey
        }
    }

    @Test
    void shouldKnowIfItIsAPbeKey() {
        ZuulDataConstants.PBE_KEY_ALGORITHMS.each {
            def key = new EncryptionKey(algorithm: it)
            assert !key.isPgpKey
            assert key.isPbeKey
        }
    }

    @Test
    void shouldCastToPgpKey() {
        def publicKeyText = new ClassPathResource("/gpg/acme/acme-public-key.asc").inputStream.text
        def key = new EncryptionKey(algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP , password: publicKeyText)
        def pgpKey = key as PGPPublicKey
        assert pgpKey.getFingerprint().encodeHex().toString() == "03e226a6555d680b0e0db91eac95ab67ccf1a7ba"
    }

    @Test(expected = GroovyCastException)
    void shouldDErrorWhenCastingToPgpKeyIfAlgorithmIsNotValid() {
        def publicKeyText = new ClassPathResource("/gpg/acme/acme-public-key.asc").inputStream.text
        def key = new EncryptionKey(password: publicKeyText)
        key as PGPPublicKey
    }

    @Test
    void shouldEnsurePublicKeyIsValid() {
        def publicKeyText = new ClassPathResource("/gpg/acme/acme-public-key.asc").inputStream.text
        def key = new EncryptionKey(algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP , password: publicKeyText)
        assert key.isValidIfPublicKeyAlgorithm()
        key.password = "not a valid key"
        assert !key.isValidIfPublicKeyAlgorithm()
    }

    @Test
    void shouldNotEnsurePublicKeyIsValidIfNotPgpAlgorithm() {
        assert key.isValidIfPublicKeyAlgorithm()
    }
}
