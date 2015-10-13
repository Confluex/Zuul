package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.model.Settings
import com.confluex.zuul.data.model.SettingsGroup
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsGroupDao extends JpaRepository<SettingsGroup, Integer> {
    SettingsGroup findBySettingsAndEnvironment(Settings settings, Environment environment)

    List<SettingsGroup> findBySettings(Settings settings)

    List<SettingsGroup> findByKey(EncryptionKey key)
}
