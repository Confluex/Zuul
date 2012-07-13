package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.Application
import org.springframework.data.repository.PagingAndSortingRepository

public interface ApplicationDao extends PagingAndSortingRepository<Application, Long> {

}