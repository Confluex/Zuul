package com.confluex.zuul.web

import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.model.Settings
import com.confluex.zuul.data.model.SettingsAudit
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup
import com.confluex.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.multipart.MultipartFile

import static org.mockito.Mockito.*

class SettingsServicesControllerTest {

    SettingsServicesController controller

    @Before
    void createController() {
        controller = new SettingsServicesController(zuulService: mock(ZuulService))
    }

    @Test
    void shouldCreateTreeMenuOfSettingsGroupedByFolders() {
        def settings = [
                new Settings(folder: "Human Resources", name: "hr-services"),
                new Settings(folder: "Human Resources", name: "hr-database"),
                new Settings(folder: "Human Resources", name: "hr-etl"),
                new Settings(folder: "Finance", name: "finance-database"),
                new Settings(folder: "Finance", name: "finance-application"),
                new Settings(folder: "Finance", name: "finance-services"),
                new Settings(folder: "Misc", name: "joe's skunkworks config"),
                new Settings(folder: null, name: "base-ldap"),
                new Settings(folder: null, name: "base-sftp"),
                new Settings(folder: null, name: "base-archive")
        ]
        when(controller.zuulService.listSettings()).thenReturn(settings)
        def request = new MockHttpServletRequest(contextPath: "/config")
        def menu = controller.menu(request)

        assert menu.size() == 6

        def finance = menu[0]
        assert finance.name == "Finance"
        assert finance.leafs.size() == 3
        assert finance.leafs[0].name == "finance-application"
        assert finance.leafs[0].resourceUri == "/config/settings/finance-application"
        assert finance.leafs[1].name == "finance-database"
        assert finance.leafs[1].resourceUri == "/config/settings/finance-database"
        assert finance.leafs[2].name == "finance-services"
        assert finance.leafs[2].resourceUri == "/config/settings/finance-services"

        def hr = menu[1]
        assert hr.name == "Human Resources"
        assert hr.leafs.size() == 3
        assert hr.leafs[0].name == "hr-database"
        assert hr.leafs[0].resourceUri == "/config/settings/hr-database"
        assert hr.leafs[1].name == "hr-etl"
        assert hr.leafs[1].resourceUri == "/config/settings/hr-etl"
        assert hr.leafs[2].name == "hr-services"
        assert hr.leafs[2].resourceUri == "/config/settings/hr-services"

        def misc = menu[2]
        assert misc.name == "Misc"
        assert misc.leafs.size() == 1
        assert misc.leafs[0].name == "joe's skunkworks config"
        assert misc.leafs[0].resourceUri == "/config/settings/joe%27s+skunkworks+config"

        assert menu[3].name == "base-archive"
        assert menu[3].resourceUri == "/config/settings/base-archive"
        assert menu[4].name == "base-ldap"
        assert menu[4].resourceUri == "/config/settings/base-ldap"
        assert menu[5].name == "base-sftp"
        assert menu[5].resourceUri == "/config/settings/base-sftp"
    }

    @Test
    void createFromPropertiesFileShouldInvokeServiceWithFileInputStream() {
        def multipartFile = mock(MultipartFile)
        def inputStream = mock(InputStream)
        when(multipartFile.getInputStream()).thenReturn(inputStream)
        def view = controller.createFromProperties(multipartFile, "foo", "dev")
        verify(controller.zuulService).createSettingsGroupFromPropertiesFile("foo", "dev", inputStream)
        assert view == "redirect:/settings/foo#dev"
    }

    @Test
    void renderPropertiesByNameAndEnvRenderPropertiesFile() {
        def mockResponse = new MockHttpServletResponse()
        def group = new SettingsGroup(settings: new Settings(name: "my-application"), environment: new Environment(name: "dev"))
        group.entries.add(new SettingsEntry(key: "jdbc.driver", value: "com.awesome.db.Driver"))
        group.entries.add(new SettingsEntry(key: "jdbc.username", value: "maxPower"))

        when(controller.zuulService.findSettingsGroupByNameAndEnvironment(group.name, group.environment.name)).thenReturn(group)
        controller.renderPropertiesByNameAndEnv(mockResponse, group.name, group.environment.name)
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment(group.name, group.environment.name)

        def content = mockResponse.contentAsString
        assert content
        def properties = new Properties()
        properties.load(new StringReader(content))
        assert properties['jdbc.driver'] == "com.awesome.db.Driver"
        assert properties['jdbc.username'] == "maxPower"
    }

    @Test
    void deletePropertiesByNameAndEnvShouldInvokeServiceAndReturnCorrectResponseCode() {
        def response = new MockHttpServletResponse()
        def group = new SettingsGroup(id: 123)
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-config", "dev")).thenReturn(group)
        controller.deletePropertiesByNameAndEnv(response, "test-config", "dev")
        verify(controller.zuulService).deleteSettingsGroup(group)
        assert response.status == 204
    }


    @Test
    void shouldRenderSettingsGroupsAsJson() {
        def settings = [
                new Settings(name: 'group-a').addToGroups(new SettingsGroup(environment: new Environment(name: "dev")))
        ]
        when(controller.zuulService.listSettings()).thenReturn(settings)
        def results = controller.listJson(new MockHttpServletRequest())
        verify(controller.zuulService).listSettings()
        assert results == [[name: 'group-a', environment: 'dev', resourceUri: '/settings/dev/group-a']]
    }

    @Test
    void shouldEncryptEntry() {
        def expected = new SettingsEntry(id: 1, key: "a.b.c", value: "foo", encrypted: false)
        when(controller.zuulService.encryptSettingsEntryValue(expected.id)).thenReturn(expected)
        when(controller.zuulService.save(expected, SettingsAudit.AuditType.ENCRYPT)).thenReturn(expected)
        def result = controller.encrypt(expected.id)
        verify(controller.zuulService).encryptSettingsEntryValue(expected.id)
        verify(controller.zuulService).save(expected, SettingsAudit.AuditType.ENCRYPT)
        assert result.is(expected)
    }

    @Test
    void shouldDecryptEntry() {
        def expected = new SettingsEntry(id: 1, key: "a.b.c", value: "foo", encrypted: true)
        when(controller.zuulService.decryptSettingsEntryValue(expected.id)).thenReturn(expected)
        when(controller.zuulService.save(expected, SettingsAudit.AuditType.DECRYPT)).thenReturn(expected)
        def result = controller.decrypt(expected.id)
        verify(controller.zuulService).decryptSettingsEntryValue(expected.id)
        verify(controller.zuulService).save(expected, SettingsAudit.AuditType.DECRYPT)
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
    void shouldUpdateSettingsEntry() {
        def formEntry = new SettingsEntry(key: "a", value: "b")
        def dbEntry = new SettingsEntry(id: 100, key: "not a", value: "not b", group: new SettingsGroup(id: 1))

        when(controller.zuulService.findSettingsEntry(100)).thenReturn(dbEntry)
        when(controller.zuulService.save(dbEntry)).thenReturn(dbEntry)
        def resultEntry = controller.updateEntryJson(100, formEntry)
        verify(controller.zuulService).save(dbEntry)
        assert resultEntry.id == 100
        assert resultEntry.key == "a"
        assert resultEntry.value == "b"
        assert !resultEntry.encrypted
        assert resultEntry.group.id == 1
        assert resultEntry.is(dbEntry)
    }

    @Test
    void shouldUpdateEncryptFlagWhenSavingSettingsEntry() {
        def formEntry = new SettingsEntry(key: "a", value: "b", encrypted: true)
        def dbEntry = new SettingsEntry(id: 100, key: "not a", value: "not b", group: new SettingsGroup(id: 1))
        when(controller.zuulService.findSettingsEntry(100)).thenReturn(dbEntry)
        when(controller.zuulService.save(dbEntry)).thenReturn(dbEntry)
        def resultEntry = controller.updateEntryJson(100, formEntry)
        verify(controller.zuulService).save(dbEntry)
        assert resultEntry.encrypted
    }

    @Test
    void deleteEntryJsonShouldInvokeServiceAndReturnCorrectResponseCode() {
        def response = new MockHttpServletResponse()
        def entry = new SettingsEntry(id: 123)
        when(controller.zuulService.findSettingsEntry(entry.id)).thenReturn(entry)
        controller.deleteEntryJson(123, response)
        verify(controller.zuulService).deleteSettingsEntry(entry)
        assert response.status == 204
    }

    @Test
    void shouldRenderSettingsGroupForGivenNameAndEnvironment() {
        def expected = new SettingsGroup(entries: [
                new SettingsEntry(key: "a", value: "1"),
                new SettingsEntry(key: "b", value: "2", encrypted: true),
        ])
        def response = new MockHttpServletResponse()
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-config", "qa")).thenReturn(expected)
        def result = controller.showByNameAndEnvJson("test-config", "qa", response)
        assert result == [a: '1', b: 'ENC(2)']
        assert response.status == MockHttpServletResponse.SC_OK
    }

    @Test
    void shouldErrorWithProperStatusCodeWhenRendingSettingsAndResultsAreNotFound() {
        def response = new MockHttpServletResponse()
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-config", "qa")).thenReturn(null)
        def result = controller.showByNameAndEnvJson("test-config", "qa", response)
        assert response.status == MockHttpServletResponse.SC_NOT_FOUND
        assert result == null
    }

}
