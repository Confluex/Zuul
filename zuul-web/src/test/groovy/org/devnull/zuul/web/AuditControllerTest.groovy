package org.devnull.zuul.web

import org.devnull.security.service.AuditService
import org.junit.Before

import static org.mockito.Mockito.*
import org.junit.Test
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.security.audit.AuditRevision

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController(auditService: mock(AuditService))
    }

    @Test
    void shouldListAudits() {
        def audits = [new AuditRevision<SettingsEntry>()]
        when(controller.auditService.findAllByEntity(SettingsEntry)).thenReturn(audits)
        def mv = controller.list()
        assert mv.viewName == "/audit/index"
        assert mv.model.audits == audits
    }
}
