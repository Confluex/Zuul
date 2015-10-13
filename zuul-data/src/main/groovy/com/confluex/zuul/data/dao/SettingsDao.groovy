package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.Settings
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsDao extends JpaRepository<Settings, Integer> {
    Settings findByName(String name)
}
