package org.devnull.zuul.web

import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*
import static org.devnull.zuul.web.config.ZuulWebConstants.FLASH_ALERT_MESSAGE
import static org.devnull.zuul.web.config.ZuulWebConstants.FLASH_ALERT_TYPE
import org.springframework.web.servlet.mvc.support.RedirectAttributes

class SystemAdminControllerTest {
    SystemAdminController controller

    @Before
    void createController() {
        controller = new SystemAdminController(securityService: mock(SecurityService), zuulService: mock(ZuulService))
    }

    @Test
    void shouldListAllUsersWithCorrectModelAndView() {
        def users = [new User(id: 1), new User(id: 2)]
        def roles = [new Role(id: 1), new Role(id: 2)]
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

    @Test
    void shouldRenderCreateKeyForm() {
        assert controller.displayCreateKeyForm() == "/system/createKey"
    }

    @Test
    void shouldCreateNewKey() {
        def redirectAttributes = mock(RedirectAttributes)
        def key = new EncryptionKey(name: "test")
        assert controller.createKey(key, redirectAttributes) == "redirect:/system/keys"
        verify(controller.zuulService).saveKey(key)
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_MESSAGE, "Key ${key.name} Created")
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_TYPE, "success")
    }

}
