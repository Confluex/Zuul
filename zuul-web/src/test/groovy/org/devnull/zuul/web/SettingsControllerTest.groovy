package org.devnull.zuul.web

import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.mockito.Mockito.*

public class SettingsControllerTest {

    SettingsController controller

    @Before
    void createController() {
        controller = new SettingsController(zuulService: mock(ZuulService))
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
    void newSettingsGroupFormShouldReturnCorrectView() {
        def view = controller.newSettingsGroupForm()
        assert view == "/settings/create"
    }

    @Test
    void addEntryFormShouldHaveCorrectViewNameAndModelValue() {
        def mv = controller.addEntryForm("testGroup", "testEnvironment")
        assert mv.viewName == "/settings/entry"
    }

    @Test
    void addEntrySubmitShouldAddNewEntryToCorrectGroupAndRedirectToCorrectView() {
        def environmentName = 'testEnvironment'
        def groupName = 'testGroup'
        def group = new SettingsGroup(name: groupName)
        def entry = new SettingsEntry(key: 'a', value: 'b')

        when(controller.zuulService.findSettingsGroupByNameAndEnvironment(groupName, environmentName)).thenReturn(group)
        def view = controller.addEntrySubmit(groupName, environmentName, entry)
        def args = ArgumentCaptor.forClass(SettingsEntry)
        verify(controller.zuulService).save(args.capture())

        assert args.value.group == group
        assert args.value.key == entry.key
        assert args.value.value == entry.value
        assert view == "redirect:/settings/testGroup#testEnvironment"
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
        def mv = controller.createFromUpload("foo", "dev")
        assert mv.viewName == "/settings/upload"
        assert mv.model.environment == "dev"
        assert mv.model.groupName == "foo"
    }

    @Test
    void createFromCopySubmitShouldFindCorrectEntryToCopyAndSave() {
        def groupToCopy = new SettingsGroup(id:1, name:"some-config", environment: new Environment(name: "qa"))
        when(controller.zuulService.findSettingsGroupByNameAndEnvironment("some-config", "qa")).thenReturn(groupToCopy)
        def view = controller.createFromCopySubmit("foo", "dev", "/qa/some-config.properties")
        verify(controller.zuulService).findSettingsGroupByNameAndEnvironment("some-config", "qa")
        verify(controller.zuulService).createSettingsGroupFromCopy("foo", "dev", groupToCopy)
        assert view == "redirect:/settings/foo#dev"
    }


}
