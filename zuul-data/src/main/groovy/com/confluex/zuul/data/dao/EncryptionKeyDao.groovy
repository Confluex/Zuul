package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.EncryptionKey
import org.springframework.data.jpa.repository.JpaRepository

interface EncryptionKeyDao extends JpaRepository<EncryptionKey, String> {
}
