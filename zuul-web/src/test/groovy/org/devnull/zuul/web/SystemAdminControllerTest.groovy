package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*
import org.devnull.security.model.User
import org.devnull.security.model.Role
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.ZuulService

class SystemAdminControllerTest {
    SystemAdminController controller

    @Before
    void createController() {
        controller = new SystemAdminController(securityService: mock(SecurityService), zuulService: mock(ZuulService))
    }

    @Test
    void shouldListAllUsersWithCorrectModelAndView() {
        def users = [new User(id:1), new User(id: 2)]
        def roles = [new Role(id:1), new Role(id: 2)]
        when(controller.securityService.listUsers()).thenReturn(users)
        when(controller.securityService.listRoles()).thenReturn(roles)
        def mv = controller.listUsers()
        verify(controller.securityService).listUsers()
        assert mv.viewName == "/system/users"
        assert mv.model.users.is(users)
        assert mv.model.roles.is(roles)
    }

    @Test
    void shouldListEncryptionKeysWithCorrectModelAndView() {
        def expected = [new EncryptionKey(name: "test")]
        when(controller.zuulService.listEncryptionKeys()).thenReturn(expected)
        def mv = controller.listKeys()
        assert mv.model.keys == expected
        assert mv.viewName == "/system/keys"
    }
}
