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
        def users = [userA: new User(id: 1)]
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

    // ------ Group Filters
    @Test
    void shouldFilterAuditsByGroupWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_GROUP_NAME, "test.a.b")
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        controller.applySessionFilters(request, pagination)
        assert pagination.filter.size() == 1
        assert pagination.filter.groupName == "test.a.b"
    }


    @Test
    void shouldAddGroupToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addGroupFilter(request, "test.a.b")
        assert request.session.getAttribute(AuditController.SESSION_GROUP_NAME) == "test.a.b"
    }

    @Test
    void shouldRemoveGroupFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_GROUP_NAME, "test.a.b")
        assert "redirect:/audit" == controller.removeGroupFilter(request)
        assert !request.session.getAttribute(AuditController.SESSION_GROUP_NAME)
    }

    // ------ Modified By Filters

    @Test
    void shouldFilterAuditsByUserWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_MODIFIED_BY, "userA")
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        controller.applySessionFilters(request, pagination)
        assert pagination.filter.size() == 1
        assert pagination.filter.modifiedBy == "userA"
    }

    @Test
    void shouldAddModifiedByToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addModifiedByFilter(request, "userA")
        assert request.session.getAttribute(AuditController.SESSION_MODIFIED_BY) == "userA"
    }

    @Test
    void shouldRemoveModifiedByFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_MODIFIED_BY, "userB")
        assert "redirect:/audit" == controller.removeModifiedByFilter(request)
        assert !request.session.getAttribute(AuditController.SESSION_MODIFIED_BY)
    }

    // ------ Key Filters
    @Test
    void shouldFilterAuditsByKeyWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_SETTINGS_ENTRY_KEY, "a.b.c")
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        controller.applySessionFilters(request, pagination)
        assert pagination.filter.size() == 1
        assert pagination.filter.settingsKey == "a.b.c"
    }

    @Test
    void shouldAddKeyToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addKeyFilter(request, "a.b.c")
        assert request.session.getAttribute(AuditController.SESSION_SETTINGS_ENTRY_KEY) == "a.b.c"
    }

    @Test
    void shouldRemoveKeyFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_SETTINGS_ENTRY_KEY, "a.b.c")
        assert "redirect:/audit" == controller.removeKeyFilter(request)
        assert !request.session.getAttribute(AuditController.SESSION_SETTINGS_ENTRY_KEY)
    }

}
