package org.devnull.zuul.web

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest

class AuditControllerTest {
    AuditController controller

    @Before
    void createController() {
        controller = new AuditController()
    }


    // ------ Group Filters

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldFilterAuditsByGroupWhenSessionAttributeIsPresent() {
//        def request = new MockHttpServletRequest()
//        request.session.setAttribute(AuditController.SESSION_GROUP_ID, 123)
//        controller.findAudits(request)
//        def args = ArgumentCaptor.forClass(AuditPagination)
//        verify(controller.auditService).findAllByEntity(eq(SettingsEntry), args.capture())
//        assert args.value.filter.size() == 1
////        def criteria = args.value.filter[0] as RelatedAuditExpression
////        assert criteria.id == 123
    }


    @Test
    void shouldAddGroupToSession() {
        def request = new MockHttpServletRequest()
        assert "redirect:/audit" == controller.addGroupFilter(request, 11)
        assert request.session.getAttribute(AuditController.SESSION_GROUP_ID) == 11
    }

    @Test
    void shouldRemoveGroupFromSession() {
        def request = new MockHttpServletRequest()
        request.session.setAttribute(AuditController.SESSION_GROUP_ID, 11)
        assert "redirect:/audit" == controller.removeGroupFilter(request)
        assert !request.session.getAttribute(AuditController.SESSION_GROUP_ID)
    }

    // ------ Modified By Filters

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldFilterAuditsByUserWhenSessionAttributeIsPresent() {
//        def request = new MockHttpServletRequest()
//        request.session.setAttribute(AuditController.SESSION_MODIFIED_BY, "userA")
//        controller.findAudits(request)
//        def args = ArgumentCaptor.forClass(AuditPagination)
//        verify(controller.auditService).findAllByEntity(eq(SettingsEntry), args.capture())
//        assert args.value.filter.size() == 1
////        def criteria = args.value.filter[0] as SimpleAuditExpression
////        assert criteria.value == "userA"
////        assert criteria.op == "="
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

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldFilterAuditsByKeyWhenSessionAttributeIsPresent() {
//        def request = new MockHttpServletRequest()
//        request.session.setAttribute(AuditController.SESSION_SETTINGS_ENTRY_KEY, "a.b.c")
//        controller.findAudits(request)
//        def args = ArgumentCaptor.forClass(AuditPagination)
//        verify(controller.auditService).findAllByEntity(eq(SettingsEntry), args.capture())
//        assert args.value.filter.size() == 1
////        def criteria = args.value.filter[0] as SimpleAuditExpression
////        assert criteria.value == "a.b.c"
////        assert criteria.op == "="
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
