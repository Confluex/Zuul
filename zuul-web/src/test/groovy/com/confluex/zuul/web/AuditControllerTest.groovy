package com.confluex.zuul.web

import groovy.util.logging.Slf4j
import com.confluex.security.model.User
import com.confluex.util.pagination.HttpRequestPagination
import com.confluex.util.pagination.Pagination
import com.confluex.util.pagination.Sort
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.service.AuditService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Mockito.*

@Slf4j
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
        def mv = controller.index(new MockHttpServletRequest(), 10)
        assert mv.viewName == "/audit/index"
        assert mv.model.containsKey("filters")
        assert mv.model.containsKey("audits")
        assert mv.model.containsKey("users")
    }


    @Test
    void shouldFilterAuditsByGroupWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_FILTERS.groupName, "test")
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        controller.applySessionFilters(request, pagination)
        assert pagination.filter.size() == 1
        assert pagination.filter.groupName == "test"
    }


    @Test
    void shouldFilterAuditsByMultipleSessionAttributesWhenPresent() {
        def request = new MockHttpServletRequest()
        AuditController.SESSION_FILTERS.each {
            def value = "${it.key} test"
            request.session.setAttribute(it.value, value)
            log.info("Added session attribute {}={}", it.value, value)
        }
        def pagination = new HttpRequestPagination<SettingsAudit>(request)
        controller.applySessionFilters(request, pagination)
        assert pagination.filter.size() == AuditController.SESSION_FILTERS.size()
        AuditController.SESSION_FILTERS.each {
            assert pagination.filter[it.key] == "${it.key} test"
        }
    }

    @Test
    void shouldAddFilterToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addSessionFilter(request, "test.a.b", "groupName")
        assert request.session.getAttribute(AuditController.SESSION_FILTERS.groupName) == "test.a.b"
    }

    @Test
    void shouldRemoveGroupFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_FILTERS.groupName, "test")
        assert "redirect:/audit" == controller.removeSessionFilter(request, "groupName")
        assert !request.session.getAttribute(AuditController.SESSION_FILTERS.groupName)
    }

    @Test
    void shouldNotAddArbitrarySessionAttributes() {
        def request = new MockHttpServletRequest()
        def attribute = "randomSessionAttribute"
        assert "redirect:/audit" == controller.addSessionFilter(request, "test", attribute)
        assert !request.session.getAttribute(attribute)
    }

    @Test
    void shouldNotRemoveArbitrarySessionAttributes() {
        def request = new MockHttpServletRequest()
        def attribute = "randomSessionAttribute"
        request.session.setAttribute(attribute, "test")
        assert "redirect:/audit" == controller.removeSessionFilter(request, attribute)
        assert request.session.getAttribute(attribute) == "test"
    }

    @Test
    void shouldFilterByGroup() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.filterByGroup(request, "dev", "test-config")
        assert request.session.getAttribute(AuditController.SESSION_FILTERS.groupName) == "test-config"
        assert request.session.getAttribute(AuditController.SESSION_FILTERS.groupEnvironment) == "dev"
    }

}
