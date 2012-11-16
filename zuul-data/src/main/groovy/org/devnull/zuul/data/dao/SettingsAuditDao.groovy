package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsAudit
import org.springframework.data.jpa.repository.JpaRepository

public interface SettingsAuditDao extends JpaRepository<SettingsAudit, Long> {

}