package org.devnull.zuul.web.security

import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.web.SettingsController
import org.devnull.zuul.web.SettingsServicesController
import org.devnull.zuul.web.test.ControllerTestMixin
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

    protected void attemptToAddNewEntry(SettingsGroup group) {
        controller.addEntrySubmit(group.name, group.environment.name, new SettingsEntry(key: "a", value: "b"), mockSuccessfulBindingResult())
    }

}
