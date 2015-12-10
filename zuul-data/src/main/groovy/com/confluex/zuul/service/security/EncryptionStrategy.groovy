package com.confluex.zuul.service.security

import com.confluex.zuul.data.model.EncryptionKey


public interface EncryptionStrategy {
    String encrypt(String value, EncryptionKey key)
    String decrypt(String value, EncryptionKey key)
    Boolean supports(EncryptionKey key)
}