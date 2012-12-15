package org.devnull.zuul.web.security

import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import org.devnull.zuul.data.model.Environment
import org.springframework.mock.web.MockHttpServletResponse
import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.web.SettingsServicesController


class SettingEntryDeleteSecurityIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    SettingsServicesController settingsServicesController

    @Autowired
    SettingsGroupDao settingsGroupDao


    @Autowired
    SettingsEntryDao settingsEntryDao


    @Test(expected=AccessDeniedException)
    void shouldNotAllowRoleUserToDeleteEntry() {
        loginAsUser(LOGIN_ROLE_USER)
        def group = settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
    }

    @Test
    void shouldAllowRoleAdminToDeleteEntry() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
        assert !settingsEntryDao.findOne(entry.id)
    }

    @Test(expected=AccessDeniedException)
    void shouldNotAllowRoleAdminToDeleteEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_ADMIN)
        def group = settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "prod"))
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
    }

    @Test()
    void shouldAllowRoleSystemAdminToDeleteEntryBelongingToRestrictedGroup() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def group = settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "prod"))
        def entry = group.entries.first()
        settingsServicesController.deleteEntryJson(entry.id, new MockHttpServletResponse())
        assert !settingsEntryDao.findOne(entry.id)
    }

}
