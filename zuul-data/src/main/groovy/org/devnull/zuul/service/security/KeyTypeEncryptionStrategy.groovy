package org.devnull.zuul.service.security

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.error.InvalidOperationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component("keyTypeEncryptionStrategy")
class KeyTypeEncryptionStrategy implements EncryptionStrategy {

    @Autowired
    @Qualifier("pbeEncryptionStrategy")
    EncryptionStrategy pbe

    @Autowired
    @Qualifier("pgpEncryptionStrategy")
    EncryptionStrategy pgp


    String encrypt(String value, EncryptionKey key) {
        if (pbe.supports(key)) return pbe.encrypt(value, key)
        if (pgp.supports(key)) return pgp.encrypt(value, key)
        throw new InvalidOperationException("Cannot encrypt keys with algorithm: ${key?.algorithm}")
    }

    String decrypt(String value, EncryptionKey key) {
        if (pbe.supports(key)) return pbe.decrypt(value, key)
        if (pgp.supports(key)) return pgp.decrypt(value, key)
        throw new InvalidOperationException("Cannot decrypt keys with algorithm: ${key?.algorithm}")
    }

    Boolean supports(EncryptionKey key) {
        return pbe.supports(key) || pgp.supports(key)
    }
}
