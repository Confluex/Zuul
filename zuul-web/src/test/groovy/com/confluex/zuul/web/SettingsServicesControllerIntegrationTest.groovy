package com.confluex.zuul.web

import com.confluex.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

public class SettingsServicesControllerIntegrationTest extends ZuulWebIntegrationTest {


    @Autowired
    SettingsServicesController controller

    @Test
    void shouldRenderSettingsGroupAsJson() {
        def results = controller.listJson(new MockHttpServletRequest())
        assert results == [
                [name: 'app-data-config', environment: 'dev', resourceUri: '/settings/dev/app-data-config'],
                [name: 'app-data-config', environment: 'qa', resourceUri: '/settings/qa/app-data-config'],
                [name: 'app-data-config', environment: 'prod', resourceUri: '/settings/prod/app-data-config'],
                [name: 'hr-service-config', environment: 'prod', resourceUri: '/settings/prod/hr-service-config']
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




    Properties loadTestProperties(String path) {
        def resource = new ClassPathResource(path)
        def properties = new Properties()
        properties.load(resource.inputStream)
        return properties
    }
}
