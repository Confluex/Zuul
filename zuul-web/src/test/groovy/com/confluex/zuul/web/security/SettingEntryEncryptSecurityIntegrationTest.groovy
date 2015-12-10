package com.confluex.zuul.web.security

import com.confluex.zuul.web.SettingsServicesController
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

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
