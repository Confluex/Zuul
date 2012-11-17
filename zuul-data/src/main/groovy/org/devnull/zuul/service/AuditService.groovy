package org.devnull.zuul.service

import org.devnull.security.model.User
import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry

interface AuditService {

    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination)

    void logAudit(User user, SettingsAudit.AuditType type, List<SettingsEntry> entries)

}
