package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.Environment
import org.springframework.data.jpa.repository.JpaRepository

interface EnvironmentDao extends JpaRepository<Environment, String> {
}
