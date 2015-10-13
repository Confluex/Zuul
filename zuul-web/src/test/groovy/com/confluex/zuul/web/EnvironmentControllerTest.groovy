package com.confluex.zuul.web

import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*
import com.confluex.zuul.web.test.ControllerTestMixin

@Mixin(ControllerTestMixin)
class EnvironmentControllerTest {
    EnvironmentController controller

    @Before
    void createController() {
        controller = new EnvironmentController(zuulService: mock(ZuulService))
    }

    @Test
    void shouldListEnvironmentsForIndexPage() {
        def environments = [new Environment(name: "a"), new Environment(name: "b")]
        when(controller.zuulService.listEnvironments()).thenReturn(environments)
        def mv = controller.list()
        assert mv.model.environments == environments
        assert mv.viewName == "/system/environments/index"
    }

    @Test
    void shouldDeleteEnvironmentByNameAndRedirectBackToIndex() {
        def view = controller.delete("a")
        verify(controller.zuulService).deleteEnvironment("a")
        assert view == "redirect:/system/environments"
    }

    @Test
    void shouldToggleRestrictedFlagAndRedirectBackToIndex() {
        def view = controller.toggleRestriction("a")
        verify(controller.zuulService).toggleEnvironmentRestriction("a")
        assert view == "redirect:/system/environments"
    }

    @Test
    void shouldCreateEnvironmentAndRedirectBackToIndex() {
        def environment = new Environment(name: "test")
        def view = controller.create(environment, mockSuccessfulBindingResult())
        verify(controller.zuulService).createEnvironment("test")
        assert view == "redirect:/system/environments"
    }

    @Test
    void shouldNotCreateInvalidEnvironments() {
        def environment = new Environment(name: "test")
        def view = controller.create(environment, mockFailureBindingResult())
        verify(controller.zuulService, never()).createEnvironment(anyString())
        assert view == "/system/environments/index"
    }

    @Test
    void shouldSortEnvironmentsWithProvidedNames() {
        def names = ["a", "c", "d"]
        def view = controller.sort(names)
        verify(controller.zuulService).sortEnvironments(names)
        assert view == "redirect:/system/environments"
    }
}
