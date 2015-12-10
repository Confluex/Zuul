package com.confluex.zuul.web.security

import com.confluex.zuul.web.SettingsServicesController
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException

class SettingEntryDeleteSecurityIntegrationTest extends SecurityWebIntegrationTest {

    @Autowired
    SettingsServicesController settingsServicesController



    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleUserToDeleteEntry() {
        loginAsUser(LOGIN_ROLE_USER)
        def group = findUnRestrictedGroup()
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
    }

    @Test
    void shouldAllowRoleAdminToDeleteEntry() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = findUnRestrictedGroup()
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
        assert !settingsEntryDao.findOne(entry.id)
    }

    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleAdminToDeleteEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = findRestrictedGroup()
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
    }

    @Test()
    void shouldAllowRoleSystemAdminToDeleteEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def group = findRestrictedGroup()
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
        assert !settingsEntryDao.findOne(entry.id)
    }

}
