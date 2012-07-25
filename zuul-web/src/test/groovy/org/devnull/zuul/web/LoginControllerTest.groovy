package org.devnull.zuul.web

import org.junit.Before
import org.junit.Test;

public class LoginControllerTest {
    LoginController controller

    @Before
    void createController() {
        controller = new LoginController()
    }
    
    @Test
    void loginShouldReturnCorrectViewName() {
        def view = controller.login()
        assert view == "/login"
    }
}
