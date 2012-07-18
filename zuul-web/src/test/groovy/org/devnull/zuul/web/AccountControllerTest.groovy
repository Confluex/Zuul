package org.devnull.zuul.web

import org.devnull.security.service.SecurityService
import org.junit.Before
import static org.mockito.Mockito.*
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.devnull.security.service.OpenIdRegistrationHandler
import org.devnull.security.model.User

public class AccountControllerTest {
    AccountController controller

    @Before
    void createController() {
        controller = new AccountController(securityService: mock(SecurityService))
    }

    @Test
    void loginShouldReturnCorrectView() {
        assert controller.login() == "/login"
    }
    
    @Test
    void profileShouldReturnCorrectView() {
        assert controller.profile() == "/profile"
    }

    @Test
    void registerShouldAddSessionTempUserToModel() {
        def mockRequest = new MockHttpServletRequest()
        def mockUser = mock(User)
        mockRequest.session.setAttribute(OpenIdRegistrationHandler.SESSION_OPENID_TEMP_USER, mockUser)
        def mv = controller.register(mockRequest)
        assert mv.viewName == "/register"
        assert mv.model.user == mockUser

    }
}
