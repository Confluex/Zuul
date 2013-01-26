package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.Settings
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsDao extends JpaRepository<Settings, Integer> {
    Settings findByName(String name)
}
