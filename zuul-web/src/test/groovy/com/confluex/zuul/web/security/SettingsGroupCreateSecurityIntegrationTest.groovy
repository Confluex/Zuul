package com.confluex.zuul.web.security

import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.web.SettingsController
import com.confluex.zuul.web.test.ControllerTestMixin
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

@Mixin(ControllerTestMixin)
class SettingsGroupCreateSecurityIntegrationTest extends SecurityWebIntegrationTest {

    @Autowired
    SettingsController controller


    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleUserToCreateGroup() {
        loginAsUser(LOGIN_ROLE_USER)
        attemptToAddNewGroup(findUnRestrictedEnvironment())
    }

    @Test
    void shouldAllowRoleAdminToCreateGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        attemptToAddNewGroup(findUnRestrictedEnvironment())
    }

    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleAdminToCreateGroupBelongingToRestrictedEnv() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        attemptToAddNewGroup(findRestrictedEnvironment())
    }

    @Test
    void shouldAllowRoleSystemAdminToCreateGroupBelongingToRestrictedEnv() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        attemptToAddNewGroup(findRestrictedEnvironment())
    }

    protected void attemptToAddNewGroup(Environment environment) {
        controller.createFromScratch("test-config", environment.name)
    }

}
