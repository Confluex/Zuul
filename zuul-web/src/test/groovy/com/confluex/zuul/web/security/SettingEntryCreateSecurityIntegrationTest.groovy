package com.confluex.zuul.web.security

import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup
import com.confluex.zuul.web.SettingsController
import com.confluex.zuul.web.SettingsServicesController
import com.confluex.zuul.web.test.ControllerTestMixin
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

@Mixin(ControllerTestMixin)
class SettingEntryCreateSecurityIntegrationTest extends SecurityWebIntegrationTest {

    @Autowired
    SettingsController controller



    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleUserToCreateEntry() {
        loginAsUser(LOGIN_ROLE_USER)
        def group = findUnRestrictedGroup()
        attemptToAddNewEntry(group)
    }



    @Test
    void shouldAllowRoleAdminToCreateEntry() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = findUnRestrictedGroup()
        attemptToAddNewEntry(group)
    }

    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleAdminToCreateEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = findRestrictedGroup()
        attemptToAddNewEntry(group)
    }

    @Test
    void shouldAllowRoleSystemAdminToCreateEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def group = findRestrictedGroup()
        attemptToAddNewEntry(group)
    }

    @Test
    void shouldNotPersistChangesIfAccessIsDenied() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = findRestrictedGroup()
        def deniedMessage = false
        try {
            attemptToAddNewEntry(group)
        } catch (AccessDeniedException e) {
            deniedMessage = e.message
        }
        assert deniedMessage == "Access is denied"
        assert findRestrictedGroup().entries.find { it.key == "a" } == null
    }

    protected void attemptToAddNewEntry(SettingsGroup group) {
        controller.addEntrySubmit(group.name, group.environment.name, new SettingsEntry(key: "a", value: "b"), mockSuccessfulBindingResult())
    }

}
