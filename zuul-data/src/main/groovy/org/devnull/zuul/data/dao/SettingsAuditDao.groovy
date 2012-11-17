package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsAudit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

public interface SettingsAuditDao extends JpaRepository<SettingsAudit, Long>, JpaSpecificationExecutor<SettingsAudit>  {

}