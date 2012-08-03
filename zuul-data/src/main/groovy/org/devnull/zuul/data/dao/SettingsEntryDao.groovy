package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.data.repository.PagingAndSortingRepository

public interface SettingsEntryDao extends PagingAndSortingRepository<SettingsEntry, Integer> {

}