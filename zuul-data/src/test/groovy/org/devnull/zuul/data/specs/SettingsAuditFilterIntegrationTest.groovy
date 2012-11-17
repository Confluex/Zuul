package org.devnull.zuul.data.specs

import org.devnull.zuul.data.dao.SettingsAuditDao
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsAuditFilterIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    SettingsAuditDao settingsAuditDao

    @Test
    void shouldFilterResultsByKey() {
        def results = settingsAuditDao.findAll(new SettingsAuditFilter(["settingsKey": "jdbc.zuul.jms.enabled"]))
        assert results.size() == 3
        results.each {
            assert it.settingsKey == "jdbc.zuul.jms.enabled"
        }
    }

    @Test
    void shouldFilterResultsByType() {
        def filter = [
                type: SettingsAudit.AuditType.ADD
        ]
        def results = settingsAuditDao.findAll(new SettingsAuditFilter(filter))
        assert results.size() == 3
        results.each {
            assert it.type == SettingsAudit.AuditType.ADD
        }
    }

    @Test
    void shouldFilterResultsByModifiedBy() {
        def filter = [
                modifiedBy: 'system'
        ]
        def results = settingsAuditDao.findAll(new SettingsAuditFilter(filter))
        assert results.size() == 6
        results.each {
            assert it.modifiedBy == 'system'
        }
    }

    @Test
    void shouldFilterResultsByKeyAndType() {
        def filter = [
                settingsKey: "jdbc.zuul.jms.enabled",
                type: SettingsAudit.AuditType.ADD
        ]
        def results = settingsAuditDao.findAll(new SettingsAuditFilter(filter))
        assert results.size() == 1
        results.each {
            assert it.settingsKey == "jdbc.zuul.jms.enabled"
            assert it.type == SettingsAudit.AuditType.ADD
        }
    }
}
