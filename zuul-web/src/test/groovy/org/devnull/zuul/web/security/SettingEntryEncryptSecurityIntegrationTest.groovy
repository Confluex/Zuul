package org.devnull.zuul.web.security

import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.web.SettingsServicesController
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.junit.Ignore

class SettingEntryEncryptSecurityIntegrationTest extends SecurityWebIntegrationTest {

    @Autowired
    SettingsServicesController settingsServicesController


    @Test(expected = AccessDeniedException)
    void shouldNotAllowRoleUserToEncryptEntry() {
        loginAsUser(LOGIN_ROLE_USER)
        def entry = findUnRestrictedGroup().entries.first()
        settingsServicesController.encrypt(entry.id)
    }

    @Test
    void shouldAllowRoleAdminToEncryptEntry() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def entry = findUnRestrictedGroup().entries.first()
        def unencrypted = entry.value
        settingsServicesController.encrypt(entry.id)
        assert settingsEntryDao.findOne(entry.id).value != unencrypted
    }

    @Test(expected = AccessDeniedException)
    @Ignore("Need to refactor security for this to work due to proxy issues. Aspectj weaving should be implemented or the code needs refactoring so that the proxy isn't bypassed")
    void shouldNotAllowRoleAdminToEncryptEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def entry = findRestrictedGroup().entries.first()
        settingsServicesController.encrypt(entry.id)
    }

    @Test
    void shouldAllowRoleSystemAdminToEncryptEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def entry = findRestrictedGroup().entries.first()
        def unencrypted = entry.value
        settingsServicesController.encrypt(entry.id)
        assert settingsEntryDao.findOne(entry.id).value != unencrypted
    }

}
