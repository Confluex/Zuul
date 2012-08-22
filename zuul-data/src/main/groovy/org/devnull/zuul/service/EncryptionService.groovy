package org.devnull.zuul.service

import org.devnull.zuul.data.model.EncryptionKey


public interface EncryptionService {
    String encrypt(String value, EncryptionKey key)
    String decrypt(String value, EncryptionKey key)
}