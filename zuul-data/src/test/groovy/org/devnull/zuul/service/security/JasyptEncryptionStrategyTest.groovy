package org.devnull.zuul.service.security

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.devnull.zuul.data.model.EncryptionKey
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.junit.Before
import org.junit.Test

class JasyptEncryptionStrategyTest {

    JasyptEncryptionStrategy strategy

    @Before
    void createStrategy() {
        def keyMetaData = [
                'ROT-13': new KeyConfiguration(algorithm: "ROT-13", hashIterations: 13, provider: "FacePalm"),
                'PBE-ABC': new KeyConfiguration(algorithm: "PBE-ABC", hashIterations: 1500)
        ]
        strategy = new JasyptEncryptionStrategy(keyMetaData: keyMetaData)
    }

    @Test(expected = IllegalArgumentException)
    void shouldThrowErrorIfAlgorithmIsNotConfigured() {
        strategy.encrypt("abc", new EncryptionKey(algorithm: 'NOT_VALID'))
    }

    @Test
    void shouldCreateEncryptorFromKeyConfigrationSettingsWithKeyPassword() {
        def mockEncryptor = new MockFor(StandardPBEStringEncryptor)
        mockEncryptor.demand.setAlgorithm { String val -> assert val =="ROT-13" }
        mockEncryptor.demand.setPassword { String val -> assert val =="abc123" }
        mockEncryptor.demand.setKeyObtentionIterations { Integer val -> assert val == 13 }
        mockEncryptor.demand.setProviderName { String val -> assert val == "FacePalm" }
        mockEncryptor.use {
            strategy.createEncryptor(new EncryptionKey(algorithm: "ROT-13", password: "abc123"))
        }
    }
}
