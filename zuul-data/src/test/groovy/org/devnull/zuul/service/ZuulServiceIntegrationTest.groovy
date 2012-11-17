package org.devnull.zuul.service

import groovy.util.logging.Slf4j
import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.annotation.NotTransactional

import javax.annotation.Resource

@Slf4j
public class ZuulServiceIntegrationTest extends ZuulDataIntegrationTest {

  @Autowired
  ZuulService zuulService

  @Resource
  ThreadPoolTaskExecutor auditExecutor

  @Autowired
  SettingsAuditDao settingsAuditDao

  @Test
  @NotTransactional
  void shouldLogAuditsWithTaskExecutor() {
    def count = settingsAuditDao.count().toInteger() // no change of loss here with this test data
    def entry = zuulService.findSettingsEntry(1)
    zuulService.logAudit(SettingsAudit.AuditType.MOD, [entry])
//    assert checkExecutionStatus(1, 5, 100)
    assert checkExecutionStatus(count + 1, 5, 100)
    def audit = settingsAuditDao.findAll(new Sort(Sort.Direction.DESC, "modifiedDate")).last()
    assert audit
  }


  // TODO find out why completedTaskCount is not incrementing... logs and debugger shows that the task executor is functioning
  Boolean checkExecutionStatus(Integer expectedCount, Integer attempts, Integer backOffFactorMs) {
    for (int i=0; i < attempts; i++) {

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
