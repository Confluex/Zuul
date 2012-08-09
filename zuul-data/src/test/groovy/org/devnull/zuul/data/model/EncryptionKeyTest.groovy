package org.devnull.zuul.data.model

import org.junit.Test

class EncryptionKeyTest {
    @Test
    void toStringShouldNotContainPassword() {
        def key = new EncryptionKey(name: "foo", password: "secret")
        assert !key.toString().contains("secret")
    }
}
