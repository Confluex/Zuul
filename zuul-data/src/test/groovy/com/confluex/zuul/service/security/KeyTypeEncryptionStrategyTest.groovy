package com.confluex.zuul.service.security

import com.confluex.zuul.data.config.ZuulDataConstants
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.service.error.InvalidOperationException
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*

class KeyTypeEncryptionStrategyTest {

    KeyTypeEncryptionStrategy strategy

    @Before
    void createStrategy() {
        strategy = new KeyTypeEncryptionStrategy(pbe: mock(EncryptionStrategy), pgp: mock(EncryptionStrategy))
    }

    @Test
    void shouldDecryptPgpKeysWithProperDelegate() {
        def key = new EncryptionKey()
        when(strategy.pgp.supports(key)).thenReturn(true)
        when(strategy.pbe.supports(key)).thenReturn(false)
        when(strategy.pgp.decrypt("test", key)).thenReturn("ok")
        assert strategy.decrypt("test", key) == "ok"
        verify(strategy.pgp).decrypt("test", key)
    }

    @Test
    void shouldEncryptPgpKeysWithProperDelegate() {
        def key = new EncryptionKey()
        when(strategy.pgp.supports(key)).thenReturn(true)
        when(strategy.pbe.supports(key)).thenReturn(false)
        when(strategy.pgp.encrypt("test", key)).thenReturn("ok")
        assert strategy.encrypt("test", key) == "ok"
        verify(strategy.pgp).encrypt("test", key)
    }


    @Test
    void shouldDecryptPbeKeysWithProperDelegate() {
        def key = new EncryptionKey()
        when(strategy.pgp.supports(key)).thenReturn(false)
        when(strategy.pbe.supports(key)).thenReturn(true)
        when(strategy.pbe.decrypt("test", key)).thenReturn("ok")
        assert strategy.decrypt("test", key) == "ok"
        verify(strategy.pbe).decrypt("test", key)
    }

    @Test
    void shouldEncryptPbeKeysWithProperDelegate() {
        def key = new EncryptionKey()
        when(strategy.pgp.supports(key)).thenReturn(false)
        when(strategy.pbe.supports(key)).thenReturn(true)
        when(strategy.pbe.encrypt("test", key)).thenReturn("ok")
        assert strategy.encrypt("test", key) == "ok"
        verify(strategy.pbe).encrypt("test", key)
    }


    @Test(expected = InvalidOperationException)
    void shouldThrowErrorWhenDecryptingWithUnsupportedKey() {
        strategy.decrypt("test", new EncryptionKey())
    }

    @Test(expected = InvalidOperationException)
    void shouldThrowErrorWhenEncryptingWithUnsupportedKey() {
        strategy.encrypt("test", new EncryptionKey())
    }
}
