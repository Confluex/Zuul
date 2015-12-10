package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.Environment
import org.springframework.data.jpa.repository.JpaRepository

interface EnvironmentDao extends JpaRepository<Environment, String> {
}
