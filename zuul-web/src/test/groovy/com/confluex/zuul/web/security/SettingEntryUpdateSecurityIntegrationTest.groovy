package com.confluex.zuul.web.security

import com.confluex.zuul.data.model.SettingsGroup
import com.confluex.zuul.web.SettingsServicesController
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException
import com.confluex.zuul.data.model.SettingsEntry

class SettingEntryUpdateSecurityIntegrationTest extends SecurityWebIntegrationTest {

    @Autowired
    SettingsServicesController settingsServicesController


    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleUserToUpdateEntry() {
        loginAsUser(LOGIN_ROLE_USER)
        def entry = updateUnrestrictedEntry()
        settingsServicesController.updateEntryJson(entry.id, entry)
    }

    @Test
    void shouldAllowRoleAdminToUpdateEntry() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def entry = updateUnrestrictedEntry()
        settingsServicesController.updateEntryJson(entry.id, entry)
        assert settingsEntryDao.findOne(entry.id).value == "updated"
    }

    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleAdminToUpdateEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def entry = updateRestrictedEntry()
        settingsServicesController.updateEntryJson(entry.id, entry)
    }

    @Test
    void shouldAllowRoleSystemAdminToUpdateEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def entry = updateRestrictedEntry()
        settingsServicesController.updateEntryJson(entry.id, entry)
        assert settingsEntryDao.findOne(entry.id).value == "updated"
    }


    protected SettingsEntry updateUnrestrictedEntry() {
        def group = findUnRestrictedGroup()
        def entry = group.entries.first()
        entry.value = "updated"
        return entry
    }

    protected SettingsEntry updateRestrictedEntry() {
        def group = findRestrictedGroup()
        def entry = group.entries.first()
        entry.value = "updated"
        return entry
    }
}
