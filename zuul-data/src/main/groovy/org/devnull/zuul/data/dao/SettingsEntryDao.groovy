package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

public interface SettingsEntryDao extends PagingAndSortingRepository<SettingsEntry, Integer>, JpaSpecificationExecutor<SettingsEntry> {

}