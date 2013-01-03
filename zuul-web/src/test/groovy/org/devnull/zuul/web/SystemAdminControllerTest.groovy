package org.devnull.zuul.web

import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.ZuulService
import org.devnull.zuul.service.security.KeyConfiguration
import org.devnull.zuul.web.test.ControllerTestMixin
import org.junit.Before
import org.junit.Test
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import static org.devnull.zuul.web.config.ZuulWebConstants.FLASH_ALERT_MESSAGE
import static org.devnull.zuul.web.config.ZuulWebConstants.FLASH_ALERT_TYPE
import static org.mockito.Mockito.*

@Mixin(ControllerTestMixin)
class SystemAdminControllerTest {
    SystemAdminController controller

    @Before
    void createController() {
        def keyMetaData = [
                'PBE-ABC': new KeyConfiguration(algorithm: "PBE-ABC", description: "ABC Test"),
                'PBE-DEF': new KeyConfiguration(algorithm: "PBE-DEF", description: "DEF Test"),
                'PBE-XYZ': new KeyConfiguration(algorithm: "PBE-XYZ", description: "XYZ Test")
        ]
        controller = new SystemAdminController(
                securityService: mock(SecurityService),
                zuulService: mock(ZuulService),
                keyMetaData: keyMetaData
        )
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
        assert mv.model.keyMetaData == controller.keyMetaData
        assert mv.viewName == "/system/keys"
    }

    @Test
    void shouldRenderCreateKeyForm() {
        def mv = controller.displayCreateKeyForm()
        assert mv.viewName == "/system/createKey"
        assert mv.model.keyMetaData == controller.keyMetaData
    }

    @Test
    void shouldCreateNewKey() {
        def redirectAttributes = mock(RedirectAttributes)
        def key = new EncryptionKey(name: "test")
        def mv = controller.createKey(key, mockSuccessfulBindingResult(), redirectAttributes)
        assert mv.viewName == "redirect:/system/keys"
        verify(controller.zuulService).saveKey(key)
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_MESSAGE, "Key ${key.name} Created")
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_TYPE, "success")
    }

    @Test
    void shouldDisplayErrorFormWhenCreatingInvalidKey() {
        def redirectAttributes = mock(RedirectAttributes)
        def key = new EncryptionKey(name: "test")
        def mv = controller.createKey(key, mockFailureBindingResult(), redirectAttributes)
        assert mv.viewName == "/system/createKey"
        assert mv.model.keyMetaData == controller.keyMetaData
    }

}
