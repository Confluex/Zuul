package com.confluex.zuul.web

import org.junit.Before
import org.junit.Test;

public class OpenIdLoginControllerTest {
    OpenIdLoginController controller

    @Before
    void createController() {
        controller = new OpenIdLoginController()
    }
    
    @Test
    void shouldHaveLoginPage() {
        def view = controller.login()
        assert view == "/login/openid"
    }
}
