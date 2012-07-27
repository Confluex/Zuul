package org.devnull.zuul.web

import org.devnull.zuul.service.ZuulService
import org.junit.Before
import static org.mockito.Mockito.*
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.Environment

public class SettingsControllerTest {

    SettingsController controller

    @Before
    void createController() {
        controller = new SettingsController(zuulService: mock(ZuulService))
    }

    @Test
    void renderPropertiesByNameAndEnvRenderPropertiesFile() {
        def mockResponse = new MockHttpServletResponse()
        def group = new SettingsGroup(name:"my-application", environment: new Environment(name:"dev"))
        group.entries.add(new SettingsEntry(key:"jdbc.driver", value: "com.awesome.db.Driver"))
        group.entries.add(new SettingsEntry(key:"jdbc.username", value: "maxPower"))

        when(controller.zuulService.findSettingsGroupByNameAndEnvironment(group.name, group.environment.name)).thenReturn(group)
        controller.renderPropertiesByNameAndEnv(mockResponse, group.name, group.environment.name)
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment(group.name, group.environment.name)

        def content = mockResponse.contentAsString
        assert content
        println content
        def properties = new Properties()
        properties.load(new StringReader(content))
        assert properties['jdbc.driver'] == "com.awesome.db.Driver"
        assert properties['jdbc.username'] == "maxPower"
    }
}
