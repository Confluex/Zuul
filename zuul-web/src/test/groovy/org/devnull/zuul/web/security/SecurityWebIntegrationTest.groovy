package org.devnull.zuul.web.security

import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.model.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.dao.SettingsEntryDao

abstract class SecurityWebIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    SettingsEntryDao settingsEntryDao


    protected SettingsGroup findRestrictedGroup() {
        return settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "prod"))
    }

    protected SettingsGroup findUnRestrictedGroup() {
        return settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
    }

}
