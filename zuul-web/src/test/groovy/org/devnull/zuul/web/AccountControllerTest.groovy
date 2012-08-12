package org.devnull.zuul.web

import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.junit.Before
import org.junit.Test
import static org.mockito.Mockito.*
import static org.devnull.zuul.data.config.ZuulDataConstants.*

public class AccountControllerTest {
    AccountController controller

    @Before
    void createController() {
        controller = new AccountController(securityService: mock(SecurityService))
    }

    @Test
    void welcomeShouldReturnCorrectView() {
        assert controller.welcome() == "/welcome"
    }

    @Test
    void profileShouldReturnCorrectView() {
        assert controller.profile() == "/profile"
    }

    @Test
    void registerSubmitShouldRemoveGuestRoleAndAddUserRole() {
        def guestRole = new Role(id: 1, name: ROLE_GUEST)
        def userRole = new Role(id: 2, name: ROLE_USER)
        def user = new User(id: 1, openId: "http://fake.openid.com", roles: [guestRole])

        when(controller.securityService.currentUser).thenReturn(user)
        when(controller.securityService.findRoleByName(userRole.name)).thenReturn(userRole)

        def formUser = new User(
                id: 2, // bad id to ensure property doesn't change
                openId: "http://hax.openid.com", // bad openId to ensure property doesn't change
                firstName: "john",
                lastName: "doe",
                email: "jdoe@fake.com"
        )
        def view = controller.registerSubmit(formUser)
        assert user.id == 1
        assert user.openId == "http://fake.openid.com"
        assert user.firstName == "john"
        assert user.lastName == "doe"
        assert user.email == "jdoe@fake.com"
        assert user.roles.size() == 1
        assert user.roles.first() == userRole
        assert view == "redirect:/welcome"

        verify(controller.securityService).updateCurrentUser(true)

    }
}
