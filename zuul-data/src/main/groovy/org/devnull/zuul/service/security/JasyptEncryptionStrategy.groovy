package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.devnull.zuul.data.model.EncryptionKey
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.util.text.BasicTextEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.Resource
import java.security.Security

@Service("jasyptEncryptionService")
@Slf4j
class JasyptEncryptionStrategy implements EncryptionStrategy {
    static {
        Security.addProvider(new BouncyCastleProvider())
    }

    @Resource(name='keyMetaData')
    Map<String, KeyConfiguration> keyMetaData

    String encrypt(String value, EncryptionKey key) {
        return createEncryptor(key).encrypt(value)
    }

    String decrypt(String value, EncryptionKey key) {
        return createEncryptor(key).decrypt(value)
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