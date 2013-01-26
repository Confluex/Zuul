package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.Settings
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsGroupDao extends JpaRepository<SettingsGroup, Integer> {
    SettingsGroup findBySettingsAndEnvironment(Settings settings, Environment environment)

    List<SettingsGroup> findBySettings(Settings settings)

    List<SettingsGroup> findByKey(EncryptionKey key)
}
