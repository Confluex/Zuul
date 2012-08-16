package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*

class SystemAdminControllerTest {
    SystemAdminController controller

    @Before
    void createController() {
        controller = new SystemAdminController(securityService: mock(SecurityService))
    }

    @Test
    void shouldListAllUsersWithCorrectModelAndView() {
        def users = []
        when(controller.securityService.listUsers()).thenReturn(users)
        def mv = controller.listUsers()
        verify(controller.securityService).listUsers()
        assert mv.viewName == "/system/users"
        assert mv.model.users.is(users)
    }
}
