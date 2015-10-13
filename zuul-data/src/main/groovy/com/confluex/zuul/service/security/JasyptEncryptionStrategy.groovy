package com.confluex.zuul.service.security

import groovy.util.logging.Slf4j
import com.confluex.zuul.data.model.EncryptionKey
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Component("pbeEncryptionStrategy")
@Slf4j
class JasyptEncryptionStrategy implements EncryptionStrategy {


    @Resource(name = 'keyMetaData')
    Map<String, KeyConfiguration> keyMetaData

    String encrypt(String value, EncryptionKey key) {
        return createEncryptor(key).encrypt(value)
    }

    String decrypt(String value, EncryptionKey key) {
        return createEncryptor(key).decrypt(value)
    }

    Boolean supports(EncryptionKey key) {
        return key?.isPbeKey
    }

    StandardPBEStringEncryptor createEncryptor(EncryptionKey key) {
        def config = keyMetaData[key.algorithm]
        if (!config) {
            log.info("Configured algorithms: {}", keyMetaData.keySet())
            throw new IllegalArgumentException("Algorithm ${key.algorithm} is not supported")
        }
        def encryptor = new StandardPBEStringEncryptor()
        encryptor.algorithm = config.algorithm
        encryptor.password = key.password
        encryptor.keyObtentionIterations = config.hashIterations
        if (config.provider)
            encryptor.providerName = config.provider
        return encryptor
    }

}