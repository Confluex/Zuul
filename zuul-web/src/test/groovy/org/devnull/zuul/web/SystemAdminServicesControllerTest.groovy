package org.devnull.zuul.web

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse

import static org.mockito.Mockito.*
import org.devnull.security.service.SecurityService

class SystemAdminServicesControllerTest {

    SystemAdminServicesController controller

    @Before
    void createController() {
        controller = new SystemAdminServicesController(securityService: mock(SecurityService))
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
}
