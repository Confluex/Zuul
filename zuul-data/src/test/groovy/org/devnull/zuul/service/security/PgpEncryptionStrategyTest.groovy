package org.devnull.zuul.service.security

import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.error.InvalidOperationException
import org.junit.Before
import org.junit.Test

class PgpEncryptionStrategyTest {
    PgpEncryptionStrategy strategy

    @Before
    void createStrategy() {
        strategy = new PgpEncryptionStrategy()
    }

    @Test
    void shouldNotSupportPbeKeyAlgorithms() {
        ZuulDataConstants.PBE_KEY_ALGORITHMS.each {
            assert !strategy.supports(new EncryptionKey(algorithm: it))
        }
    }

    @Test
    void shouldSupportPgpAlgorithms() {
        ZuulDataConstants.PGP_KEY_ALGORITHMS.each {
            assert strategy.supports(new EncryptionKey(algorithm: it))
        }
    }

    @Test
    void shouldNotSupportKeyNullValues() {
        assert !strategy.supports(null)
        assert !strategy.supports(new EncryptionKey(algorithm: null))
    }

    @Test(expected = InvalidOperationException)
    void shouldErrorOnDecryptOperations() {
        strategy.decrypt("encrypteddata", new EncryptionKey())
    }
}
