package org.devnull.zuul.web

import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletResponse

public class SettingsServicesControllerIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    SettingsServicesController controller

    @Test
    void listJsonShouldContainEntriesIfDeepFetchIsTrue() {
        def environments = ["dev", "qa", "prod"]
        def results = controller.listJson(true)
        assert results.size() == 4
        environments.each { env ->
            def group = results.find { it.name == "app-data-config" && it.environment.name == env }
            assert group.entries
            loadTestProperties("/test-app-data-config-${env}.properties") == group as Properties
        }
    }

    @Test
    void listJsonShouldNotContainEntriesIfDeepFetchIsFalse() {
        def environments = ["dev", "qa", "prod"]
        def results = controller.listJson(false)
        assert results.size() == 4
        environments.each { env ->
            def group = results.find { it.environment.name == env }
            assert !group.entries
        }
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
