package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.EncryptionKey
import org.springframework.data.jpa.repository.JpaRepository

interface EncryptionKeyDao extends JpaRepository<EncryptionKey, String> {
}
