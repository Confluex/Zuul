package com.confluex.zuul.web

import org.junit.Before
import org.junit.Test;

public class LdapLoginControllerTest {
    LdapLoginController controller

    @Before
    void createController() {
        controller = new LdapLoginController()
    }

    @Test
    void shouldHaveLoginPage() {
        def view = controller.login()
        assert view == "/login/form"
    }
}
