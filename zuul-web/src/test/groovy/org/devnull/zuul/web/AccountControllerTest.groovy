package org.devnull.zuul.web

import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.ZuulService
import org.devnull.zuul.web.test.ControllerTestMixin
import org.junit.Before
import org.junit.Test
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import static org.devnull.zuul.data.config.ZuulDataConstants.*
import static org.devnull.zuul.web.config.ZuulWebConstants.*
import static org.mockito.Mockito.*

@Mixin(ControllerTestMixin)
public class AccountControllerTest {
  AccountController controller

  @Before
  void createController() {
    controller = new AccountController(securityService: mock(SecurityService), zuulService: mock(ZuulService))
  }

  @Test
  void welcomeShouldReturnCorrectView() {
    assert controller.welcome() == "/account/welcome"
  }

  @Test
  void profileShouldReturnCorrectView() {
    assert controller.profile() == "/account/profile"
  }

  @Test
  void shouldUpdateCurrentUsersProfileWithoutReAuthenticate() {
    def redirectAttributes = mock(RedirectAttributes)
    def formUser = new User(firstName: "newFirst", lastName: "newLast", email: "new@devnull.org")
    def currentUser = new User(id: 1, firstName: "oldFirst", lastName: "oldLast", email: "old@devnull.org")
    currentUser.addToRoles(new Role(name: "ROLE_USER"))

    when(controller.securityService.getCurrentUser()).thenReturn(currentUser)
    def view = controller.saveProfile(redirectAttributes, formUser, mockSuccessfulBindingResult())
    verify(controller.securityService).updateCurrentUser(false)
    verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_MESSAGE, "Updated Profile")
    verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_TYPE, "success")
    assert currentUser.id == 1
    assert currentUser.firstName == "newFirst"
    assert currentUser.lastName == "newLast"
    assert currentUser.email == "new@devnull.org"
    assert currentUser.roles.size() == 1
    assert currentUser.roles.first().name == "ROLE_USER"
    assert view == "redirect:/account/profile"
  }

  @Test
  void shouldValidateUserProfile() {
    def redirectAttributes = mock(RedirectAttributes)
    def user = new User(id: 1, firstName: "oldFirst", lastName: "oldLast", email: "old@devnull.org")
    def view = controller.saveProfile(redirectAttributes, user, mockFailureBindingResult())
    verify(controller.securityService, never()).updateCurrentUser(false)
    assert view == "/account/profile"
  }

  @Test
  void registerSubmitShouldRemoveGuestRoleAndAddUserRole() {
    def guestRole = new Role(id: 1, name: ROLE_GUEST)
    def userRole = new Role(id: 2, name: ROLE_USER)
    def user = new User(id: 1, userName: "http://fake.openid.com", roles: [guestRole])

    when(controller.securityService.currentUser).thenReturn(user)
    when(controller.securityService.findRoleByName(userRole.name)).thenReturn(userRole)

    def formUser = new User(
            id: 2, // bad id to ensure property doesn't change
            userName: "http://hax.openid.com", // bad openId to ensure property doesn't change
            firstName: "john",
            lastName: "doe",
            email: "jdoe@fake.com"
    )
    def flash = mock(RedirectAttributes)
    def view = controller.registerSubmit(formUser, flash)
    assert user.id == 1
    assert user.userName == "http://fake.openid.com"
    assert user.firstName == "john"
    assert user.lastName == "doe"
    assert user.email == "jdoe@fake.com"
    assert user.roles.size() == 1
    assert user.roles.first() == userRole
    assert view == "redirect:/account/profile"

    verify(controller.securityService).updateCurrentUser(true)
    verify(flash).addFlashAttribute(FLASH_ALERT_MESSAGE, "Registration Complete")
    verify(flash).addFlashAttribute(FLASH_ALERT_TYPE, "success")
  }

  @Test
  void requestPermissionsShouldReturnCorrectViewWithRoles() {
    def roles = [new Role(id: 1)]
    when(controller.securityService.listRoles()).thenReturn(roles)
    def mv = controller.requestPermissions()
    assert mv.model.roles.is(roles)
    assert mv.viewName == "/account/permissions"
  }

  @Test
  void submitPermissionsRequestShouldReturnCorrectView() {
    def view = controller.submitPermissionsRequest("ROLE_TEST")
    verify(controller.zuulService).notifyPermissionsRequest("ROLE_TEST")
    assert view == "/account/requested"
  }
}
