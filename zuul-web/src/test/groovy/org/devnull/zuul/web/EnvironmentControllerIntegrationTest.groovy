package org.devnull.zuul.web

import org.junit.Test

import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsGroupDao


class EnvironmentControllerIntegrationTest extends ZuulWebIntegrationTest {
    @Autowired
    EnvironmentDao environmentDao

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    EnvironmentController controller

    @Test
    void shouldCascadeDeleteEnvironmentSettings() {
        loginAsUser(LOGIN_ROLE_SYSTEM_ADMIN)
        def env = environmentDao.findOne("prod")
        def groups = env.groups
        assert groups
        controller.delete("prod")
        assert !environmentDao.findOne("prod")
        groups.each {
            assert !settingsGroupDao.findOne(it.id)
        }
    }
}
