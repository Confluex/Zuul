package org.devnull.zuul.web

import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse

import static org.mockito.Mockito.*

public class SettingsControllerTest {

    SettingsController controller

    @Before
    void createController() {
        controller = new SettingsController(zuulService: mock(ZuulService))
    }

    @Test
    void renderPropertiesByNameAndEnvRenderPropertiesFile() {
        def mockResponse = new MockHttpServletResponse()
        def group = new SettingsGroup(name: "my-application", environment: new Environment(name: "dev"))
        group.entries.add(new SettingsEntry(key: "jdbc.driver", value: "com.awesome.db.Driver"))
        group.entries.add(new SettingsEntry(key: "jdbc.username", value: "maxPower"))

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

    @Test
    void listJsonShouldReturnResultsFromService() {
        def expected = [new SettingsGroup(name: "a")]
        when(controller.zuulService.listSettingsGroups()).thenReturn(expected)
        def results = controller.listJson()
        verify(controller.zuulService).listSettingsGroups()
        assert results.is(expected)
    }

    @Test
    void showShouldGroupResultsByEnvironment() {
        def environments = [
                new Environment(name: "dev"),
                new Environment(name: "qa"),
                new Environment(name: "prod")
        ]
        def groups = [
                new SettingsGroup(name: "group-1", environment: environments[0]),
                new SettingsGroup(name: "group-1", environment: environments[1]),
                new SettingsGroup(name: "group-1", environment: environments[2])
        ]

        when(controller.zuulService.listEnvironments()).thenReturn(environments)
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment('group-1', 'dev')).thenReturn(groups[0])
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment('group-1', 'qa')).thenReturn(groups[1])
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment('group-1', 'prod')).thenReturn(groups[2])

        def mv = controller.show("group-1")

        assert mv.viewName == "/settings/show"
        assert mv.model.environments.is(environments)
        assert mv.model.groupsByEnv instanceof Map
        environments.each { env ->
            assert mv.model.groupsByEnv[env] == groups.find { it.environment == env }
        }
        assert mv.model.groupName == "group-1"
    }

    @Test
    void encryptionShouldReturnResultsFromService() {
        def expected = new SettingsEntry(id: 1, key: "a.b.c", value: "foo", encrypted: false)
        when(controller.zuulService.encryptSettingsEntryValue(expected.id)).thenReturn(expected)
        def result = controller.encrypt(expected.id)
        verify(controller.zuulService).encryptSettingsEntryValue(expected.id)
        assert result.is(expected)
    }

    @Test
    void decryptionShouldReturnResultsFromService() {
        def expected = new SettingsEntry(id: 1, key: "a.b.c", value: "foo", encrypted: true)
        when(controller.zuulService.decryptSettingsEntryValue(expected.id)).thenReturn(expected)
        def result = controller.decrypt(expected.id)
        verify(controller.zuulService).decryptSettingsEntryValue(expected.id)
        assert result.is(expected)
    }

    @Test
    void showEntryJsonShouldReturnResultsFromService() {
        def expected = new SettingsEntry(id: 1)
        when(controller.zuulService.findSettingsEntry(1)).thenReturn(expected)
        def result = controller.showEntryJson(1)
        assert result.is(expected)
    }

    @Test
    void updateEntryJsonShouldBindToResultsFromService() {
        def formEntry = new SettingsEntry(id: -1, key: "a", value: "b")
        def persistedEntry = new SettingsEntry(id: 100, key: "c", value: "d")
        when(controller.zuulService.findSettingsEntry(100)).thenReturn(persistedEntry)
        when(controller.zuulService.save(persistedEntry)).thenReturn(persistedEntry)
        def resultEntry = controller.updateEntryJson(100, formEntry)
        assert resultEntry.is(persistedEntry)
        assert resultEntry.id == 100
        assert resultEntry.key == "a"
        assert resultEntry.value == "b"

    }

    @Test
    void deleteEntryJsonShouldInvokeServiceAndReturnCorrectResponseCode() {
        def response = new MockHttpServletResponse()
        controller.deleteEntryJson(123, response)
        verify(controller.zuulService).deleteSettingsEntry(123)
        assert response.status == 204
    }

    @Test
    void newSettingsGroupFormShouldReturnCorrectView() {
        def view = controller.newSettingsGroupForm()
        assert view == "/settings/new"
    }

    @Test
    void addEntryJsonShouldAddToCorrectGroupAndSaveAndRedirect() {
        def response = new MockHttpServletResponse()
        def entry = new SettingsEntry(id: 1)
        def group = new SettingsGroup(name: "testName", environment: new Environment(name: "testEnv"))
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("testName", "testEnv")).thenReturn(group)
        when(controller.zuulService.save(group)).thenReturn(group)
        def view = controller.addEntryJson(response, "testName", "testEnv", entry)
        assert group.entries.contains(entry)
        assert entry.group == group
        assert view == "redirect:/settings/entry/1.json"
    }

    @Test
    void createFromScratchShouldInvokeServiceAndRedirectToCorrectView() {
        def group = new SettingsGroup(name: "foo", environment: new Environment(name:"dev"))
        when(controller.zuulService.createEmptySettingsGroup("foo", "dev")).thenReturn(group)
        def view = controller.createFromScratch("foo", "dev")
        verify(controller.zuulService).createEmptySettingsGroup("foo", "dev")
        assert view == "redirect:/settings/foo#dev"
    }
}
