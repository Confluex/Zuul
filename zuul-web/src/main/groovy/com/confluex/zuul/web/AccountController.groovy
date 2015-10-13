package com.confluex.zuul.web

import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import com.confluex.zuul.service.ZuulService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.validation.Valid

import static com.confluex.zuul.data.config.ZuulDataConstants.*
import static com.confluex.zuul.web.config.ZuulWebConstants.*

@Controller
class AccountController {
  final def log = LoggerFactory.getLogger(this.class)

  @Autowired
  SecurityService securityService

  @Autowired
  ZuulService zuulService


  @ModelAttribute("user")
  User findAccountFromSecurityContext() {
    return securityService.currentUser
  }

  @RequestMapping(value = "/account/profile", method = RequestMethod.GET)
  String profile() {
    return "/account/profile"
  }

  @RequestMapping(value = "/account/profile", method = RequestMethod.POST)
  String saveProfile(RedirectAttributes redirectAttrs, @ModelAttribute("user") @Valid User formUser, BindingResult result) {
    if (result.hasErrors()) {
      return "/account/profile"
    }
    log.info("Saving user profile: {}", formUser)
    def user = securityService.currentUser
    user.firstName = formUser.firstName
    user.lastName = formUser.lastName
    user.email = formUser.email
    securityService.updateCurrentUser(false)
    redirectAttrs.addFlashAttribute(FLASH_ALERT_MESSAGE, "Updated Profile")
    redirectAttrs.addFlashAttribute(FLASH_ALERT_TYPE, "success")
    return "redirect:/account/profile"
  }

  @RequestMapping(value = "/account/register", method = RequestMethod.GET)
  String register() {
    return "/account/register"
  }

  @RequestMapping(value = "/account/register", method = RequestMethod.POST)
  String registerSubmit(RedirectAttributes redirectAttrs, @ModelAttribute @Valid User user, BindingResult result) {
    if (result.hasErrors()) {
      return "/account/register"
    }
    def currentUser = securityService.currentUser
    currentUser.firstName = user.firstName
    currentUser.lastName = user.lastName
    currentUser.email = user.email
    registerCurrentUser(ROLE_USER)
    redirectAttrs.addFlashAttribute(FLASH_ALERT_MESSAGE, "Registration Complete")
    redirectAttrs.addFlashAttribute(FLASH_ALERT_TYPE, "success")
    return "redirect:/account/profile"
  }

  @RequestMapping("/account/welcome")
  String welcome() {
    return "/account/welcome"
  }

  @RequestMapping("/account/permissions")
  ModelAndView requestPermissions() {
    return new ModelAndView("/account/permissions", [roles: securityService.listRoles()])
  }

  @RequestMapping("/account/permissions/{roleName}")
  String submitPermissionsRequest(@PathVariable("roleName") String roleName) {
    zuulService.notifyPermissionsRequest(roleName)
    return "/account/requested"
  }

  protected void registerCurrentUser(String role) {
    def currentUser = securityService.currentUser
    currentUser.addToRoles(securityService.findRoleByName(role))
    currentUser.roles.removeAll {
      it.name == ROLE_GUEST
    }
    securityService.updateCurrentUser(true)
  }
}
