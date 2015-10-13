package com.confluex.zuul.data.model

import org.junit.Test

class AuditTypeTest {
    @Test
    void shouldHaveHumanFriendlyToString() {
        assert SettingsAudit.AuditType.ADD.toString() == "Add"
        assert SettingsAudit.AuditType.DELETE.toString() == "Delete"
        assert SettingsAudit.AuditType.MOD.toString() == "Modify"
    }

    @Test
    void shouldHaveActionAttribute() {
        assert SettingsAudit.AuditType.ADD.action == "added"
        assert SettingsAudit.AuditType.DELETE.action == "deleted"
        assert SettingsAudit.AuditType.MOD.action == "modified"
    }
}
