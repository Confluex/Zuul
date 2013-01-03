package org.devnull.zuul.data.model

import org.junit.Before
import org.junit.Test

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
}
