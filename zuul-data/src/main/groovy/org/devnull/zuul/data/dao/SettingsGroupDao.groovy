package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.data.repository.PagingAndSortingRepository
import org.devnull.zuul.data.model.EncryptionKey

interface SettingsGroupDao extends PagingAndSortingRepository<SettingsGroup, Integer> {
    SettingsGroup findByNameAndEnvironment(String name, Environment environment)
    List<SettingsGroup> findByName(String name)
    List<SettingsGroup> findByKey(EncryptionKey key)
}
