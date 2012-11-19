package org.devnull.zuul.service

import groovy.util.logging.Slf4j
import org.devnull.security.dao.UserDao
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.annotation.NotTransactional

import javax.annotation.Resource

@Slf4j
public class AuditServiceIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    AuditService service

    @Resource
    ThreadPoolTaskExecutor auditExecutor

    @Autowired
    SettingsAuditDao settingsAuditDao

    @Autowired
    SettingsEntryDao settingsEntryDao

    @Autowired
    UserDao userDao

    @Test
    @NotTransactional
    void shouldLogAuditsWithTaskExecutor() {
        def count = settingsAuditDao.count().toInteger() // no loss with this test data set
        def entry = settingsEntryDao.findOne(1)
        def user = userDao.findOne(1)
        service.logAudit(user, entry, SettingsAudit.AuditType.MOD)
//    assert checkExecutionStatus(1, 5, 100)
        assert checkExecutionStatus(count + 1, 5, 100)
        def audit = settingsAuditDao.findAll(new Sort(Sort.Direction.DESC, "modifiedDate")).first()
        assert audit.encrypted == entry.encrypted
        assert audit.groupEnvironment == entry.group.environment.name
        assert audit.groupName == entry.group.name
        assert audit.modifiedBy == user.userName
        assert audit.modifiedDate.clearTime() == new Date().clearTime()
        assert audit.settingsKey == entry.key
        assert audit.settingsValue == entry.value
        assert audit.type == SettingsAudit.AuditType.MOD
    }

    // TODO find out why completedTaskCount is not incrementing... logs and debugger shows that the task executor is functioning
    Boolean checkExecutionStatus(Integer expectedCount, Integer attempts, Integer backOffFactorMs) {
        for (int i = 0; i < attempts; i++) {
//      def completedCount = auditExecutor.threadPoolExecutor.completedTaskCount
            def completedCount = settingsAuditDao.count()
            if (completedCount < expectedCount) {
                def sleepTime = backOffFactorMs * i
                log.info("Completed task count: {} lower than expected: {}. Sleeping {}ms ", completedCount, expectedCount, sleepTime)
                sleep(sleepTime)
            }
            else {
                return true
            }
        }
        return false
    }
}
