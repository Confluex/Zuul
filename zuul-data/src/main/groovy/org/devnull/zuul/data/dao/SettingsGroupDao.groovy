package org.devnull.zuul.data.dao

import org.springframework.data.repository.PagingAndSortingRepository
import org.devnull.zuul.data.model.SettingsGroup

interface SettingsGroupDao extends PagingAndSortingRepository<SettingsGroup, Long> {

}
