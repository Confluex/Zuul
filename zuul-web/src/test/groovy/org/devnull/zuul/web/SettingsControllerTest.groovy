package org.devnull.zuul.web

import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.Settings
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.devnull.zuul.web.test.ControllerTestMixin
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

@Mixin(ControllerTestMixin)
public class SettingsControllerTest {

    SettingsController controller

    @Before
    void createController() {
        controller = new SettingsController(zuulService: mock(ZuulService))
    }

    @Test
    void shouldListAllSettings() {
        def settings = [
                new Settings(id: 1),
                new Settings(id: 2)
        ]
        when(controller.zuulService.listSettings()).thenReturn(settings)
        def mv = controller.index()
        assert mv.viewName == "/settings/index"
        assert mv.model.settings == settings
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
            //noinspection GroovyAssignabilityCheck
            assert mv.model.groupsByEnv[env] == groups.find { it.environment == env }
        }
        assert mv.model.groupName == "group-1"
    }

    @Test
    void shouldRedirectToGroupEnvironmentTab() {
        assert controller.showGroup("dev", "test-config") == "redirect:/settings/test-config#dev"
    }

    @Test
    void newSettingsGroupFormShouldReturnCorrectView() {
        def view = controller.newSettingsGroupForm()
        assert view == "/settings/create"
    }

    @Test
    void addEntryFormShouldHaveCorrectViewNameAndModelValue() {
        def group = new SettingsGroup()
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("testGroup", "testEnvironment")).thenReturn(group)
        def mv = controller.addEntryForm("testGroup", "testEnvironment")
        assert mv.viewName == "/settings/entry"
        assert mv.model.group.is(group)
    }

    @Test
    void shouldCreateNewEntriesForGroup() {
        def environmentName = 'testEnvironment'
        def groupName = 'testGroup'
        def group = new SettingsGroup(id: 1, name: groupName)
        def entry = new SettingsEntry(key: 'a', value: 'b', group: group)

        when(controller.zuulService.findSettingsGroupByNameAndEnvironment(groupName, environmentName)).thenReturn(group)
        def mv = controller.addEntrySubmit(groupName, environmentName, entry, mockSuccessfulBindingResult())
        verify(controller.zuulService).createEntry(group, entry)
        assert mv.viewName == "redirect:/settings/testGroup#testEnvironment"
    }

    @Test
    void addEntrySubmitShouldReturnFormViewWhenValidationErrorsExist() {
        def entry = mock(SettingsEntry)
        def group = new SettingsGroup()
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("group", "env")).thenReturn(group)
        def mv = controller.addEntrySubmit("group", "env", entry, mockFailureBindingResult())
        verify(controller.zuulService, never()).save(Matchers.any(SettingsEntry))
        assert mv.viewName == "/settings/entry"
        assert mv.model.group.is(group)
    }

    @Test
    void createFromScratchShouldInvokeServiceAndRedirectToCorrectView() {
        def group = new SettingsGroup(name: "foo", environment: new Environment(name: "dev"))
        when(controller.zuulService.createEmptySettingsGroup("foo", "dev")).thenReturn(group)
        def view = controller.createFromScratch("foo", "dev")
        verify(controller.zuulService).createEmptySettingsGroup("foo", "dev")
        assert view == "redirect:/settings/foo#dev"
    }

    @Test
    void createFromUploadShouldDisplayCorrectViewAndModel() {
        def mv = controller.createFromUpload("foo", "dev")
        assert mv.viewName == "/settings/upload"
        assert mv.model.environment == "dev"
        assert mv.model.groupName == "foo"
    }

    @Test
    void createFromCopyShouldDisplayCorrectViewAndModel() {
        def mv = controller.createFromCopy("foo", "dev")
        assert mv.viewName == "/settings/copy"
        assert mv.model.environment == "dev"
        assert mv.model.groupName == "foo"
    }

    @Test
    void createFromCopySubmitShouldFindCorrectEntryToCopyAndSave() {
        def groupToCopy = new SettingsGroup(id: 1, name: "some-config", environment: new Environment(name: "qa"))
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("some-config", "qa")).thenReturn(groupToCopy)
        def view = controller.createFromCopySubmit("foo", "dev", "/qa/some-config.properties")
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment("some-config", "qa")
        verify(controller.zuulService).createSettingsGroupFromCopy("foo", "dev", groupToCopy)
        assert view == "redirect:/settings/foo#dev"
    }


    @Test
    void shouldChangeGroupKeyAndRedirect() {
        def group = new SettingsGroup(id: 1, environment: new Environment(name: "dev"), key: new EncryptionKey(name: "old-key"))
        def newKey = new EncryptionKey(name: "test-key")
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-app", "dev")).thenReturn(group)
        when(controller.zuulService.findKeyByName("test-key")).thenReturn(newKey)
        def view = controller.changeGroupKey("dev", "test-app", "test-key", null)
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment("test-app", "dev")
        verify(controller.zuulService).findKeyByName("test-key")
        verify(controller.zuulService).changeKey(group, newKey)
        assert view == "redirect:/settings/test-app#dev"
    }

    @Test
    void shouldReturnConfirmPageWhenChangingToPgpKeyIfNotConfirmed() {
        def group = new SettingsGroup(id: 1, environment: new Environment(name: "dev"), key: new EncryptionKey(name: "old-key"))
        def newKey = new EncryptionKey(name: "test-key", algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP)
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-app", "dev")).thenReturn(group)
        when(controller.zuulService.findKeyByName("test-key")).thenReturn(newKey)

        def view = controller.changeGroupKey("dev", "test-app", "test-key", false)
        verify(controller.zuulService, never()).changeKey(any(SettingsGroup), any(EncryptionKey))
        assert view == "/settings/pgpKeyConfirm"

        view = controller.changeGroupKey("dev", "test-app", "test-key", true)
        verify(controller.zuulService).changeKey(any(SettingsGroup), any(EncryptionKey))
        assert view == "redirect:/settings/test-app#dev"
    }

    @Test
    void shouldReturnErrorPageWhenChangingToPgpKeyAndValuesAreEncrypted() {
        def oldKey = new EncryptionKey(name: "old-key", algorithm: ZuulDataConstants.KEY_ALGORITHM_PGP)
        def newKey = new EncryptionKey(name: "test-key", algorithm: ZuulDataConstants.KEY_ALGORITHM_AES)
        def group = new SettingsGroup(id: 1, environment: new Environment(name: "dev"), key: oldKey)
        group.addToEntries(new SettingsEntry(encrypted: true))
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-app", "dev")).thenReturn(group)
        when(controller.zuulService.findKeyByName("test-key")).thenReturn(newKey)

        def view = controller.changeGroupKey("dev", "test-app", "test-key", false)
        verify(controller.zuulService, never()).changeKey(any(SettingsGroup), any(EncryptionKey))
        assert view == "/error/pgpKeyChange"

        group.entries.each { it.encrypted = false }
        view = controller.changeGroupKey("dev", "test-app", "test-key", true)
        verify(controller.zuulService).changeKey(any(SettingsGroup), any(EncryptionKey))
        assert view == "redirect:/settings/test-app#dev"
    }

    @Test
    void shouldGroupSearchResultsByGroup() {
        def groupA = new SettingsGroup(settings: new Settings(name: "groupA"))
        def groupB = new SettingsGroup(settings: new Settings(name: "groupA"))
        def entries = [
                new SettingsEntry(key: "abc", value: '123', group: groupA),
                new SettingsEntry(key: "abc", value: '456', group: groupB),
                new SettingsEntry(key: "abcdef", value: 'false', group: groupA)
        ]
        when(controller.zuulService.search(eq("abc"), Matchers.any(Pagination))).thenReturn(entries)
        def mv = controller.search("abc", new MockHttpServletRequest())
        assert mv.model.results == [
                (groupA): [entries[0], entries[2]],
                (groupB): [entries[1]]
        ]
        assert mv.viewName == "/settings/search"
    }
}
