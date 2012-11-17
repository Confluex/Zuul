package org.devnull.zuul.service

import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry

interface AuditService {

    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination)

    void logAudit(SettingsAudit.AuditType type, List<SettingsEntry> entries)

}
