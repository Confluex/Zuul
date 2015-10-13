package com.confluex.zuul.web

import com.confluex.zuul.data.dao.EncryptionKeyDao
import com.confluex.zuul.data.dao.SettingsGroupDao
import com.confluex.zuul.service.ZuulService
import com.confluex.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse

class SystemAdminServicesControllerIntegrationTest extends ZuulWebIntegrationTest {
    @Autowired
    SystemAdminServicesController controller

    @Autowired
    ZuulService zuulService

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    EncryptionKeyDao encryptionKeyDao

    @Test
    void shouldDeleteKeyAndChangeEffectedGroupsToDefaultKeyAndReEncrypt() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)

        def defaultKey = zuulService.findDefaultKey()
        def key = encryptionKeyDao.findOne("Human Resources Key")
        def effectedGroups = settingsGroupDao.findByKey(key)

        assert key
        assert defaultKey
        assert key != defaultKey
        assert effectedGroups.find { it.key == key }

        controller.deleteKeyByName(new MockHttpServletResponse(), key.name)
        assert !zuulService.findKeyByName("Human Resources Key")

        def defaultKeyGroups = settingsGroupDao.findByKey(defaultKey)
        assert defaultKeyGroups.containsAll(effectedGroups)
    }

}
