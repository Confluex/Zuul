package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse

import static org.mockito.Mockito.*

class SystemAdminServicesControllerTest {

    SystemAdminServicesController controller

    @Before
    void createController() {
        controller = new SystemAdminServicesController(securityService: mock(SecurityService), zuulService: mock(ZuulService))
    }


    @Test
    void removeRoleFromUserShouldHaveCorrectResponseCode() {
        def mockResponse = new MockHttpServletResponse()
        controller.removeRoleFromUser(mockResponse, 1, 2)
        assert mockResponse.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void removeRoleFromUserShouldInvokeService() {
        def userId = 1234
        def roleId = 9018
        controller.removeRoleFromUser(mock(HttpServletResponse), roleId, userId)
        verify(controller.securityService).removeRoleFromUser(roleId, userId)
    }

    @Test
    void addRoleToUserShouldInvokeService() {
        def roleId = 1234
        def userId = 5678
        def response = new MockHttpServletResponse()
        controller.addRoleRoleToUser(response, roleId, userId)
        verify(controller.securityService).addRoleToUser(roleId, userId)
        assert response.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void deleteUserShouldInvokeService() {
        def response = new MockHttpServletResponse()
        controller.deleteUser(response, 123)
        verify(controller.securityService).deleteUser(123)
        assert response.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void shouldChangeDefaultKeyAndRedirectWithCorrectStatus() {
        def view = controller.setDefaultKey(new MockHttpServletResponse(), new EncryptionKey(name: "test"))
        verify(controller.zuulService).changeDefaultKey("test")
        // TODO needs feedback message
        assert view.statusCode == HttpStatus.SEE_OTHER
        assert view.contextRelative
        assert view.url == "/system/keys/default.json"
    }


    @Test
    void shouldFindDefaultKey() {
        def expected = new EncryptionKey(name: "test")
        when(controller.zuulService.findDefaultKey()).thenReturn(expected)
        def result = controller.getDefaultKey()
        assert result.is(expected)
    }
}
