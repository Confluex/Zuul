package com.confluex.zuul.service.security

import groovy.mock.interceptor.MockFor
import groovy.util.logging.Slf4j
import com.confluex.zuul.data.config.ZuulDataConstants
import com.confluex.zuul.data.model.EncryptionKey
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.junit.Before
import org.junit.Test

@Slf4j
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
        mockEncryptor.demand.setAlgorithm { String val -> assert val == "ROT-13" }
        mockEncryptor.demand.setPassword { String val -> assert val == "abc123" }
        mockEncryptor.demand.setKeyObtentionIterations { Integer val -> assert val == 13 }
        mockEncryptor.demand.setProviderName { String val -> assert val == "FacePalm" }
        mockEncryptor.use {
            strategy.createEncryptor(new EncryptionKey(algorithm: "ROT-13", password: "abc123"))
        }
    }

    @Test
    void shouldSupportPbeKeyAlgorithms() {
        ZuulDataConstants.PBE_KEY_ALGORITHMS.each {
            assert strategy.supports(new EncryptionKey(algorithm: it))
        }
    }

    @Test
    void shouldNotSupportPgpAlgorithms() {
        ZuulDataConstants.PGP_KEY_ALGORITHMS.each {
            assert !strategy.supports(new EncryptionKey(algorithm: it))
        }
    }

    @Test
    void shouldNotSupportKeyNullValues() {
        assert !strategy.supports(null)
        assert !strategy.supports(new EncryptionKey(algorithm: null))
    }

}
