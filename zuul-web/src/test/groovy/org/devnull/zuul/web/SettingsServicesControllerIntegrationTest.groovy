package org.devnull.zuul.web

import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.Environment
import org.springframework.security.access.AccessDeniedException

public class SettingsServicesControllerIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    SettingsServicesController controller

    @Test
    void shouldRenderSettingsGroupAsJson() {
        def results = controller.listJson(new MockHttpServletRequest())
        assert results == [
                [name:'app-data-config', environment:'dev', resourceUri:'/settings/dev/app-data-config.json'],
                [name:'app-data-config', environment:'qa', resourceUri: '/settings/qa/app-data-config.json'],
                [name:'app-data-config', environment:'prod', resourceUri:'/settings/prod/app-data-config.json'],
                [name:'hr-service-config', environment:'prod', resourceUri:'/settings/prod/hr-service-config.json']
        ]
    }


    @Test
    void renderPropertiesByNameAndEnvShouldReturnValidResults() {
        def response = new MockHttpServletResponse()
        controller.renderPropertiesByNameAndEnv(response, "app-data-config", "dev")
        def properties = new Properties()
        properties.load(new StringReader(response.contentAsString))
        assert properties == loadTestProperties("/test-app-data-config-dev.properties")
    }


    /* ----------- Security Tests ------------- */

    @Test(expected=AccessDeniedException)
    void shouldNotAllowRoleUserToDeleteEntry() {
        loginAsUser(OPEN_ID_USER)
        def group = settingsGroupDao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        controller.deleteEntryJson(group.entries.first().id, new MockHttpServletResponse())
    }



    Properties loadTestProperties(String path) {
        def resource = new ClassPathResource(path)
        def properties = new Properties()
        properties.load(resource.inputStream)
        return properties
    }
}
