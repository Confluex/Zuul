package org.devnull.zuul.web

import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletResponse

public class SettingsControllerIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    SettingsController controller

    @Test
    void listJsonShouldReturnValidResults() {
        def results = controller.listJson()
        println results
        assert results.size() == 3
        def dev = results.find { it.environment.name == "dev" }
        def qa = results.find { it.environment.name == "qa" }
        def prod = results.find { it.environment.name == "prod" }

        assert loadTestProperties("/test-app-data-config-dev.properties") == dev as Properties
        assert loadTestProperties("/test-app-data-config-qa.properties") == qa as Properties
        assert loadTestProperties("/test-app-data-config-prod.properties") == prod as Properties

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
