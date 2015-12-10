package com.confluex.zuul.web

import com.confluex.zuul.data.config.ZuulDataConstants
import com.confluex.zuul.service.notification.GreenMailContext
import com.confluex.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountControllerIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    AccountController controller

    @Autowired
    GreenMailContext greenMailContext

    @Test
    void shouldSendEmailsToGreenmailWhenRequestingPermissions() {
        loginAsUser(LOGIN_ROLE_USER)
        controller.submitPermissionsRequest(ZuulDataConstants.ROLE_ADMIN)
        def messages = greenMailContext.greenMail.receivedMessages
        assert messages.size() == 2
    }
}
