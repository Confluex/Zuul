package org.devnull.zuul.web

import org.devnull.security.model.User
import org.devnull.util.pagination.HttpRequestPagination
import org.devnull.util.pagination.Pagination
import org.devnull.util.pagination.Sort
import org.devnull.zuul.data.model.SettingsAudit
import org.devnull.zuul.service.AuditService
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
        controller = new AuditController(auditService: mock(AuditService))
    }

    @Test
    void shouldFindAudits() {
        def audits = [new SettingsAudit(id: 1)]
        when(controller.auditService.findSettingAudits(Matchers.any(Pagination))).thenReturn(audits)
        def results = controller.findAudits(new MockHttpServletRequest(), 25)
        assert audits == results
    }

    @Test
    void shouldFindUsersFromAudits() {
        def audits = [new SettingsAudit(id: 1)]
        def users = [userA:new User(id: 1)]
        when(controller.auditService.lookupUsersForAudits(audits)).thenReturn(users)
        def results = controller.findUsers(audits)
        assert users == results
    }

    @Test
    void shouldApplySortCriteriaWhenFindingAudits() {
        def request = new MockHttpServletRequest()
        request.setParameter("sort", "modifiedBy")
        request.setParameter("dir", "DESC")
        controller.findAudits(request, 25)
        def args = ArgumentCaptor.forClass(HttpRequestPagination)
        verify(controller.auditService).findSettingAudits(args.capture())
        assert args.value.sorts.size() == 1
        def sort = args.value.sorts.first()
        assert sort.direction == Sort.DESC
        assert sort.field == "modifiedBy"
    }

    @Test
    void shouldApplyPagingCriteriaWhenFindingAudits() {
        def request = new MockHttpServletRequest()
        request.setParameter("page", "2")
        controller.findAudits(request, 15)
        def args = ArgumentCaptor.forClass(HttpRequestPagination)
        verify(controller.auditService).findSettingAudits(args.capture())
        assert args.value.page == 1
        assert args.value.max == 15
    }

    @Test
    void shouldHaveIndexPage() {
        assert controller.index() == "/audit/index"
    }

}
