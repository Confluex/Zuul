package org.devnull.zuul.service.security

import org.devnull.zuul.data.model.EncryptionKey


public interface EncryptionStrategy {
    String encrypt(String value, EncryptionKey key)
    String decrypt(String value, EncryptionKey key)
    Boolean supports(EncryptionKey key)
}