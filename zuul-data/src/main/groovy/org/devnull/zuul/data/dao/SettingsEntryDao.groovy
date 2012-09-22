package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

public interface SettingsEntryDao extends JpaRepository<SettingsEntry, Integer>, JpaSpecificationExecutor<SettingsEntry> {

}