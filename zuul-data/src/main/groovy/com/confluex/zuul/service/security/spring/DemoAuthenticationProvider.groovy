package com.confluex.zuul.service.security.spring

import groovy.util.logging.Slf4j
import com.confluex.security.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Slf4j
@Component("demoAuthenticationProvider")
@Profile("security-demo")
class DemoAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    SecurityService securityService

    String demoPassword = "demo"

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Demo user authenticating: {}", authentication)
        def userNamePassword = authentication as UsernamePasswordAuthenticationToken
        def user = securityService.findByUserName(userNamePassword.name)
        if (!user || userNamePassword.credentials != demoPassword) {
            log.warn("Invalid demo credentials: name={}, password={}", userNamePassword?.name, userNamePassword?.credentials)
            throw new BadCredentialsException("Incorrect username or password. Demo password is: ${demoPassword}")
        }
        def auth = new TestingAuthenticationToken(user, "********", user.authorities as List)
        auth.authenticated = true
        log.debug("Created new authentication token {}", auth)
        return auth
    }

    @Override
    boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
