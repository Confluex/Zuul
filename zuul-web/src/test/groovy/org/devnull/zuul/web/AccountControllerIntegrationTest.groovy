package org.devnull.zuul.web

import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.service.notification.GreenMailContext
import org.devnull.zuul.web.test.ZuulWebIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountControllerIntegrationTest extends ZuulWebIntegrationTest {

    @Autowired
    AccountController controller

    @Autowired
    GreenMailContext greenMailContext

    @Test
    void shouldSendEmailsToGreenmailWhenRequestingPermissions() {
        loginAsUser(OPEN_ID_SYS_ADMIN)
        controller.submitPermissionsRequest(ZuulDataConstants.ROLE_ADMIN)
        def messages = greenMailContext.greenMail.receivedMessages
        assert messages.size() == 2
    }
}
