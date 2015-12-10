package com.confluex.zuul.service

import groovy.util.logging.Slf4j
import com.confluex.orm.util.JpaPaginationAdapter
import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import com.confluex.util.pagination.Pagination
import com.confluex.zuul.data.dao.SettingsAuditDao
import com.confluex.zuul.data.dao.SettingsEntryDao
import com.confluex.zuul.data.dao.SettingsGroupDao
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.model.SettingsAudit.AuditType
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup
import com.confluex.zuul.data.specs.SettingsAuditFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service("auditService")
@Slf4j
class AuditServiceImpl implements AuditService {

    @Autowired
    SettingsAuditDao settingsAuditDao

    @Autowired
    SettingsEntryDao settingsEntryDao

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    SecurityService securityService


    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination) {
        def filter = new SettingsAuditFilter(pagination.filter)
        def audits = settingsAuditDao.findAll(filter, new JpaPaginationAdapter(pagination))
        pagination.results = audits.content
        pagination.total = audits.totalElements
        return pagination
    }

    void logAudit(User user, SettingsGroup group) {
        group?.entries?.each { logAudit(user, it) }
    }

    void logAudit(User user, SettingsGroup group, AuditType type) {
        group?.entries?.each { logAudit(user, it, type) }
    }

    void logAudit(User user, SettingsEntry entry) {
        def type = entry.id ? AuditType.MOD : AuditType.ADD
        logAudit(user, entry, type)
    }

    @Async("auditExecutor")
    void logAudit(User user, SettingsEntry entry, AuditType type) {
        try {
            def audit = new SettingsAudit(
                    groupName: entry.group?.name,
                    groupEnvironment:entry.group?.environment?.name,
                    encrypted: entry.encrypted,
                    modifiedBy: user?.userName,
                    modifiedDate: new Date(),
                    settingsKey: entry.key,
                    settingsValue: entry.value,
                    type: type
            )
            if (type == AuditType.DECRYPT || type == AuditType.ENCRYPT) {
                audit.settingsValue = type.action
            }
            log.debug("Saving new audit entry: {}", audit)
            settingsAuditDao.save(audit)
        }
        catch (Exception e) {
            log.error("Unable to save audit for entry ${entry}", e)
        }
    }

    Map<String, User> lookupUsersForAudits(List<SettingsAudit> audits) {
        def users = [:]
        audits.collect { it.modifiedBy }.unique().each { modifiedBy ->
            def user = securityService.findByUserName(modifiedBy)
            if (!user) {
                user = new User(userName: modifiedBy, firstName: "Deleted", lastName: "User", email:"deleted@confluex.org")
            }
            users[modifiedBy] = user
        }
        return users
    }
}
