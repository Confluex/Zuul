package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsAuditDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsAuditDao settingsAuditDao

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
