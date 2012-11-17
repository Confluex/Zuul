package org.devnull.zuul.service

import org.devnull.security.model.User
import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry

interface AuditService {

    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination)

    void logAudit(User user, SettingsAudit.AuditType type, List<SettingsEntry> entries)

    /**
     * The audit modified by is not coupled directly to the user object to allow for users
     * to be deleted independent of auditing records. This method will attempt to lookup
     * the user or create a 'dummy' transient user in its place.
     *
     * @return map of userNames to User objects.
     */
    Map<String, User> lookupUsersForAudits(List<SettingsAudit> audits)
}
