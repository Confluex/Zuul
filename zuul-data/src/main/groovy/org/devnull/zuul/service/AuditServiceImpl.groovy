package org.devnull.zuul.service

import groovy.util.logging.Slf4j
import org.devnull.orm.util.JpaPaginationAdapter
import org.devnull.security.service.SecurityService
import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service("auditService")
@Slf4j
class AuditServiceImpl implements AuditService {

    @Autowired
    SettingsAuditDao settingsAuditDao

    @Autowired
    SecurityService securityService

    List<SettingsAudit> findSettingAudits(Pagination<SettingsAudit> pagination) {
        def audits = settingsAuditDao.findAll(new JpaPaginationAdapter(pagination))
        pagination.results = audits.content
        pagination.total = audits.totalElements
        return pagination
    }

    @Transactional(readOnly = false)
    @Async("auditExecutor")
    void logAudit(SettingsAudit.AuditType type, List<SettingsEntry> entries) {
        def auditDate = new Date()
        entries.each { entry ->
            try {
                def audit = new SettingsAudit(
                        groupName: entry.group?.name,
                        groupEnvironment: entry.group?.environment?.name,
                        encrypted: entry.encrypted,
                        modifiedBy: securityService.currentUser?.userName,
                        modifiedDate: auditDate,
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
    }
}
