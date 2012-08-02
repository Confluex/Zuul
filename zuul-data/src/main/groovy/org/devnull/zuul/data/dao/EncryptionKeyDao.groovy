package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.EncryptionKey
import org.springframework.data.repository.PagingAndSortingRepository

interface EncryptionKeyDao extends PagingAndSortingRepository<EncryptionKey, String> {
}
