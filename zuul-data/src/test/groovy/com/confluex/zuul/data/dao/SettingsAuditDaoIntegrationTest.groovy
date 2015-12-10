package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsAuditDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsAuditDao settingsAuditDao

    @Test
    void findOneShouldRetrieveAndMapResults() {
        def audit = settingsAuditDao.findOne(1L)
        assert audit.id == 1L
        assert audit.groupName == "app-data-config"
        assert audit.groupEnvironment == "dev"
        assert audit.settingsKey == "jdbc.zuul.jms.enabled"
        assert audit.settingsValue == "true"
        assert !audit.encrypted
        assert audit.modifiedBy == "system"
        assert audit.modifiedDate.format("MM/dd/yy") == "10/31/12"
        assert audit.type == SettingsAudit.AuditType.ADD
    }

    @Test
    void shouldInsertRecord() {
        def count = settingsAuditDao.count()
        def audit = settingsAuditDao.save(new SettingsAudit(
                encrypted: false,
                groupName: "group a",
                modifiedBy: "userA",
                modifiedDate: new Date(),
                settingsKey: "a.b.c",
                settingsValue: 123
        ))
        assert settingsAuditDao.count() == count + 1
        assert audit == settingsAuditDao.findOne(audit.id)
    }
}
