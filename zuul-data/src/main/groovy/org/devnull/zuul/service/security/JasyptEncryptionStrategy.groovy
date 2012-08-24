package org.devnull.zuul.service.security

import org.devnull.zuul.data.model.EncryptionKey
import org.jasypt.util.text.BasicTextEncryptor
import org.springframework.stereotype.Service

@Service("jasyptEncryptionService")
class JasyptEncryptionStrategy implements EncryptionStrategy {

    String encrypt(String value, EncryptionKey key) {
        def encryptor = new BasicTextEncryptor();
        encryptor.password = key.password
        return encryptor.encrypt(value)
    }

    String decrypt(String value, EncryptionKey key) {
        def encryptor = new BasicTextEncryptor();
        encryptor.password = key.password
        return encryptor.decrypt(value)
    }
}
