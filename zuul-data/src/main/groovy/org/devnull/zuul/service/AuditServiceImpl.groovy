package org.devnull.zuul.service

import groovy.util.logging.Slf4j
import org.devnull.orm.util.JpaPaginationAdapter
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsAudit.AuditType
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.specs.SettingsAuditFilter
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

    void logAuditDeleteByGroupId(User user, Integer groupId) {
        def group = settingsGroupDao.findOne(groupId)
        group.entries.each { logAudit(user, it, AuditType.DELETE) }
    }

    void logAudit(User user, SettingsGroup group) {
        group?.entries?.each { logAudit(user, it) }
    }

    void logAudit(User user, SettingsEntry entry) {
        def type = entry.id ? AuditType.MOD : AuditType.ADD
        logAudit(user, entry, type)
    }

    @Async("auditExecutor")
    void logAudit(User user, SettingsEntry entry, AuditType type) {
        try {
            def group = settingsGroupDao.findOne(entry.group?.id)
            def audit = new SettingsAudit(
                    groupName: group?.name,
                    groupEnvironment:group?.environment?.name,
                    encrypted: entry.encrypted,
                    modifiedBy: user?.userName,
                    modifiedDate: new Date(),
                    settingsKey: entry.key,
                    settingsValue: entry.value,
                    type: type
            )
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
                user = new User(userName: modifiedBy, firstName: "Deleted", lastName: "User", email:"deleted@devnull.org")
            }
            users[modifiedBy] = user
        }
        return users
    }
}
