package com.confluex.zuul.service

import com.confluex.security.model.User
import com.confluex.util.pagination.Pagination
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.model.SettingsAudit.AuditType
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup

interface AuditService {

    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination)

    void logAudit(User user, SettingsGroup group)

    void logAudit(User user, SettingsGroup group,  AuditType type)

    void logAudit(User user, SettingsEntry entry)

    void logAudit(User user, SettingsEntry entry, AuditType type)

    /**
     * The audit modified by is not coupled directly to the user object to allow for users
     * to be deleted independent of auditing records. This method will attempt to lookup
     * the user or create a 'dummy' transient user in its place.
     *
     * @return map of userNames to User objects.
     */
    Map<String, User> lookupUsersForAudits(List<SettingsAudit> audits)
}
