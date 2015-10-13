package com.confluex.zuul.service.security.spring

import com.confluex.security.model.Role
import com.confluex.security.model.User
import com.confluex.security.service.SecurityService
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.openid.OpenIDAuthenticationToken

import static org.mockito.Mockito.*

class DemoAuthenticationProviderTest {
    DemoAuthenticationProvider provider

    @Before
    void createProvider() {
        provider = new DemoAuthenticationProvider(demoPassword: "foo", securityService: mock(SecurityService))
    }

    @Test
    void shouldOnlySupportUsernamePasswordAuthenticationTokens() {
        assert provider.supports(UsernamePasswordAuthenticationToken)
        assert !provider.supports(OpenIDAuthenticationToken)
    }

    @Test
    void shouldAuthenticateUsersWithValidNameAndPassword() {
        def user = new User(userName: "validUser", roles: [new Role(name: "ROLE_A"), new Role(name: "ROLE_B")])
        when(provider.securityService.findByUserName(user.userName)).thenReturn(user)
        def auth = provider.authenticate(new UsernamePasswordAuthenticationToken("validUser", provider.demoPassword))
        assert auth.principal == user
        assert auth.authenticated
        assert auth.authorities.size() == 2
        assert auth.authorities.find { it.authority == "ROLE_A"}
        assert auth.authorities.find { it.authority == "ROLE_B"}
    }

    @Test(expected = BadCredentialsException)
    void shouldThrowCorrectErrorIfUserDoesNotExist() {
        provider.authenticate(new UsernamePasswordAuthenticationToken("invalidUser", provider.demoPassword))
    }

    @Test(expected = BadCredentialsException)
    void shouldThrowCorrectErrorIfPasswordIsIncorrect() {
        def user = new User(userName: "validUser", roles: [new Role(name: "ROLE_A"), new Role(name: "ROLE_B")])
        when(provider.securityService.findByUserName(user.userName)).thenReturn(user)
        provider.authenticate(new UsernamePasswordAuthenticationToken("validUser", "invalidPassword"))
    }
}
