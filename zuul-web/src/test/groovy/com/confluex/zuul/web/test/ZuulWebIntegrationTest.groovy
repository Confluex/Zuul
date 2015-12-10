package com.confluex.zuul.web.test

import com.confluex.security.service.SecurityService
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.TestingAuthenticationProvider
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [
'classpath:zuul-data-context.xml',
'classpath:zuul-web-context.xml',
'classpath:security-context.xml'
])
@Transactional('transactionManager')
@ActiveProfiles(["test", "security-openid"])
abstract class ZuulWebIntegrationTest {


    @Autowired
    ProviderManager authenticationManager

    @Autowired
    SecurityService securityService

    protected final def LOGIN_ROLE_SYSTEM_ADMIN = "https://me.yahoo.com/a/mMz2C510uMjhwvHr.4K2aToLWzrPDJb.._M-#b431e"
    protected final def LOGIN_ROLE_ADMIN = "https://www.google.com/accounts/o8/id?id=AItOawle6AU5ND9pprX_GAsLn6OP8aL8lXaxypg"
    protected final def LOGIN_ROLE_USER = "https://www.google.com/accounts/o8/id?id=AItOawnlnuHfoKGwMJSjRHxBROwqil0OE84Zscc"

    @Before
    void addTestAuthenticationProvider() {
        authenticationManager.providers.add(new TestingAuthenticationProvider())
    }

    @After
    void logout() {
        SecurityContextHolder.clearContext()
    }

    protected void loginAsUser(String userName) {
        def user = securityService.findByUserName(userName)
        def testUser = new TestingAuthenticationToken(user, "fake", user.authorities as List)
        def response = authenticationManager.authenticate(testUser)
        SecurityContextHolder.getContext().setAuthentication(response);
    }

}
