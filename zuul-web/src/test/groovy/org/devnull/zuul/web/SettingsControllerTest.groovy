package org.devnull.zuul.web

import org.devnull.util.pagination.Pagination
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.devnull.zuul.web.test.ControllerTestMixin
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Matchers.eq
import static org.mockito.Mockito.*

@Mixin(ControllerTestMixin)
public class SettingsControllerTest {

    SettingsController controller

    @Before
    void createController() {
        controller = new SettingsController(zuulService: mock(ZuulService))
    }

    @Test
    void shouldListAllDistinctGroupNames() {
        def groups = [
                new SettingsGroup(name: "a", environment: new Environment(name: "prod")),
                new SettingsGroup(name: "a", environment: new Environment(name: "qa")),
                new SettingsGroup(name: "a", environment: new Environment(name: "dev")),
                new SettingsGroup(name: "b", environment: new Environment(name: "dev")),
                new SettingsGroup(name: "b", environment: new Environment(name: "qa"))
        ]
        when(controller.zuulService.listSettingsGroups()).thenReturn(groups)
        def mv = controller.index()
        assert mv.viewName == "/settings/index"
        assert mv.model.groups == ["a", "b"]
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
    void addEntrySubmitShouldSaveRecord() {
        def environmentName = 'testEnvironment'
        def groupName = 'testGroup'
        def group = new SettingsGroup(id: 1, name: groupName)
        def entry = new SettingsEntry(key: 'a', value: 'b', group: group)

        def mv = controller.addEntrySubmit(groupName, environmentName, entry, mockSuccessfulBindingResult())
        def args = ArgumentCaptor.forClass(SettingsEntry)
        verify(controller.zuulService).save(args.capture())

        assert args.value.group == group
        assert args.value.key == entry.key
        assert args.value.value == entry.value
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
    void shouldChangeGroupKeyAnAndRedirect() {
        def group = new SettingsGroup(id: 1, environment: new Environment(name: "dev"))
        def key = new EncryptionKey(name: "test-key")
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("test-app", "dev")).thenReturn(group)
        when(controller.zuulService.findKeyByName("test-key")).thenReturn(key)
        def view = controller.changeGroupKey("dev", "test-app", "test-key")
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment("test-app", "dev")
        verify(controller.zuulService).findKeyByName("test-key")
        verify(controller.zuulService).changeKey(group, key)
        assert view == "redirect:/settings/test-app#dev"
    }

    @Test
    void shouldGroupSearchResultsByGroup() {
        def groupA = new SettingsGroup(name: "groupA")
        def groupB = new SettingsGroup(name: "groupB")
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
