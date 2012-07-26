package org.devnull.zuul.data.dao

import org.springframework.data.repository.PagingAndSortingRepository
import org.devnull.zuul.data.model.Environment

interface EnvironmentDao extends PagingAndSortingRepository<Environment, String> {
}
