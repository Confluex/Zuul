package org.devnull.zuul.web

import org.devnull.security.audit.AuditPagination
import org.devnull.security.audit.AuditRevision
import org.devnull.security.model.User
import org.devnull.security.model.UserRevision
import org.devnull.security.service.AuditService
import org.devnull.zuul.data.model.SettingsEntry
import org.hibernate.envers.query.criteria.RelatedAuditExpression
import org.hibernate.envers.query.criteria.SimpleAuditExpression
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController(auditService: mock(AuditService))
    }

    @Test
    void shouldFindAudits() {
        def audits = [new AuditRevision<SettingsEntry>(revision: new UserRevision(modifiedBy: "a"))]
        when(controller.auditService.findAllByEntity(eq(SettingsEntry), Matchers.any(AuditPagination))).thenReturn(audits)
        assert audits == controller.findAudits(new MockHttpServletRequest())
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldFilterAuditsByUserWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_MODIFIED_BY, "userA")
        controller.findAudits(request)
        def args = ArgumentCaptor.forClass(AuditPagination)
        verify(controller.auditService).findAllByEntity(eq(SettingsEntry), args.capture())
        assert args.value.filter.size() == 1
        def criteria = args.value.filter[0] as SimpleAuditExpression
        assert criteria.value == "userA"
        assert criteria.op == "="
//        assert criteria.propertyNameGetter.get() // ugh.. this stuff is so not test friendly :(
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldFilterAuditsByGroupWhenSessionAttributeIsPresent() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_GROUP_ID, 123)
        controller.findAudits(request)
        def args = ArgumentCaptor.forClass(AuditPagination)
        verify(controller.auditService).findAllByEntity(eq(SettingsEntry), args.capture())
        assert args.value.filter.size() == 1
        def criteria = args.value.filter[0] as RelatedAuditExpression
        assert criteria.id == 123
    }

    @Test
    void shouldFindUsersWhoCreatedRevisions() {
        def audits = [new AuditRevision(), new AuditRevision()]
        def expected = [a: new User(id: 1), b: new User(id: 2)]
        when(controller.auditService.collectUsersFromRevisions(audits)).thenReturn(expected)
        assert controller.findUsers(audits) == expected
    }

    @Test
    void shouldAddGroupToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addGroupFilter(request, 11)
        assert request.session.getAttribute(AuditController.SESSION_GROUP_ID) == 11
    }

    @Test
    void shoulRemoveGroupFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_GROUP_ID, 11)
        assert "redirect:/audit" == controller.removeGroupFilter(request)
        assert !request.session.getAttribute(AuditController.SESSION_GROUP_ID)
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
}
