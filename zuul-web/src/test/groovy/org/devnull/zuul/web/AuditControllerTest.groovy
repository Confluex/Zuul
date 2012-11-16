package org.devnull.zuul.web

import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Mockito.*

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController(zuulService: mock(ZuulService))
    }

    @Test
    void shouldFindAudits() {
        def audits = [new SettingsAudit(id:1)]
        when(controller.zuulService.findSettingAudits(Matchers.any(Pagination))).thenReturn(audits)
        def results = controller.findAudits(new MockHttpServletRequest())
        assert audits == results
    }

    @Test
    void shouldHaveIndexPage() {
        assert controller.index() == "/audit/index"
    }

}
