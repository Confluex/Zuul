package org.devnull.zuul.web

import org.devnull.util.pagination.HttpRequestPagination
import org.devnull.util.pagination.Pagination
import org.devnull.util.pagination.Sort
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
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
    void shouldApplySortCriteriaWhenFindingAudits() {
        def request = new MockHttpServletRequest()
        request.setParameter("sort", "modifiedBy")
        request.setParameter("dir", "DESC")
        controller.findAudits(request)
        def args = ArgumentCaptor.forClass(HttpRequestPagination)
        verify(controller.zuulService).findSettingAudits(args.capture())
        assert args.value.sorts.size() == 1
        def sort = args.value.sorts.first()
        assert sort.direction == Sort.DESC
        assert sort.field == "modifiedBy"
    }

    @Test
    void shouldHaveIndexPage() {
        assert controller.index() == "/audit/index"
    }

}
