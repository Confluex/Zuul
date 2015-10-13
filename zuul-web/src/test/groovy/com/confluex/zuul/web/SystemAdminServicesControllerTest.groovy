package com.confluex.zuul.web

import com.confluex.security.service.SecurityService
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.service.ZuulService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse

import static org.mockito.Mockito.*
import com.confluex.zuul.data.model.Environment

class SystemAdminServicesControllerTest {

    SystemAdminServicesController controller

    @Before
    void createController() {
        controller = new SystemAdminServicesController(securityService: mock(SecurityService), zuulService: mock(ZuulService))
    }


    @Test
    void removeRoleFromUserShouldHaveCorrectResponseCode() {
        def mockResponse = new MockHttpServletResponse()
        controller.removeRoleFromUser(mockResponse, 1, 2)
        assert mockResponse.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void removeRoleFromUserShouldInvokeService() {
        def userId = 1234
        def roleId = 9018
        controller.removeRoleFromUser(mock(HttpServletResponse), roleId, userId)
        verify(controller.securityService).removeRoleFromUser(roleId, userId)
    }

    @Test
    void addRoleToUserShouldInvokeService() {
        def roleId = 1234
        def userId = 5678
        def response = new MockHttpServletResponse()
        controller.addRoleRoleToUser(response, roleId, userId)
        verify(controller.securityService).addRoleToUser(roleId, userId)
        assert response.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void deleteUserShouldInvokeService() {
        def response = new MockHttpServletResponse()
        controller.deleteUser(response, 123)
        verify(controller.securityService).deleteUser(123)
        assert response.status == HttpServletResponse.SC_NO_CONTENT
    }

    @Test
    void shouldChangeDefaultKeyAndRedirectWithCorrectStatus() {
        def view = controller.setDefaultKey(new EncryptionKey(name: "test"))
        verify(controller.zuulService).changeDefaultKey("test")
        // TODO needs feedback message
        assert view.statusCode == HttpStatus.SEE_OTHER
        assert view.contextRelative
        assert view.url == "/system/keys/default.json"
    }

    @Test
    void shouldListKeys() {
        def expected = [new EncryptionKey(name: "a"), new EncryptionKey(name: "b")]
        when(controller.zuulService.listEncryptionKeys()).thenReturn(expected)
        def keys = controller.listKeys()
        assert keys.is(expected)
    }

    @Test
    void shouldFindDefaultKey() {
        def expected = new EncryptionKey(name: "test")
        when(controller.zuulService.findDefaultKey()).thenReturn(expected)
        def result = controller.getDefaultKey()
        assert result.is(expected)
    }

    @Test
    void shouldFindKeyByName() {
        def expected = new EncryptionKey(name: "test")
        when(controller.zuulService.findKeyByName("test")).thenReturn(expected)
        def result = controller.findKeyByName("test")
        assert result.is(expected)
    }

    @Test
    void shouldUpdateKeyByNameWithoutChangingName() {
        def formKey = new EncryptionKey(name: "new name", description: "new description", password: "new password")
        def databaseKey = new EncryptionKey(name: "test", description: "test description", password: "test password")
        when(controller.zuulService.findKeyByName("test")).thenReturn(databaseKey)
        controller.updateKeyByName("test", formKey)

        def arg = ArgumentCaptor.forClass(EncryptionKey)
        verify(controller.zuulService).saveKey(arg.capture())
        assert arg.value.description == "new description"
        assert arg.value.password == "new password"
        assert arg.value.name == "test"
    }

    @Test
    void shouldUpdateKeyAndReturnResultsFromService() {
        def formKey = new EncryptionKey(name: "new name", description: "new description", password: "new password")
        def databaseKey = new EncryptionKey(name: "test", description: "test description", password: "test password")
        when(controller.zuulService.findKeyByName("test")).thenReturn(databaseKey)
        when(controller.zuulService.saveKey(Matchers.any(EncryptionKey))).thenReturn(databaseKey)
        def result = controller.updateKeyByName("test", formKey)
        assert result.is(databaseKey)
    }

    @Test
    void shouldDeleteKeyByNameAndReturnCorrectResponseCode() {
        def response = new MockHttpServletResponse()
        controller.deleteKeyByName(response, "test")
        verify(controller.zuulService).deleteKey("test")
        assert response.status == HttpServletResponse.SC_NO_CONTENT
    }
}
