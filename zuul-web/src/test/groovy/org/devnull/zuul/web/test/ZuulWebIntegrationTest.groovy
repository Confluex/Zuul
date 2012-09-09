package org.devnull.zuul.web.test

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.TestingAuthenticationProvider
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional
import org.junit.After
import org.junit.BeforeClass

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [
'classpath:zuul-data-context.xml',
'classpath:zuul-web-context.xml',
'classpath:security-context.xml'
])
@Transactional('transactionManager')
abstract class ZuulWebIntegrationTest {

    @Autowired
    ProviderManager authenticationManager

    @Autowired
    SecurityService securityService

    protected final def OPEN_ID_SYS_ADMIN = 'https://me.yahoo.com/a/mMz2C510uMjhwvHr.4K2aToLWzrPDJb.._M-#b431e'
    protected final def OPEN_ID_USER = 'https://www.google.com/accounts/o8/id?id=AItOawnlnuHfoKGwMJSjRHxBROwqil0OE84Zscc'

    @BeforeClass
    static void setDataConfigLocation() {
        System.setProperty('zuul.data.config', 'classpath:sample-data-config.properties')
    }

    @Before
    void addTestAuthenticationProvider() {
        authenticationManager.providers.add(new TestingAuthenticationProvider())
    }

    @After
    void logout() {
        SecurityContextHolder.clearContext()
    }

    protected void loginAsUser(String openId) {
        def user = securityService.findUserByOpenId(openId)
        def testUser = new TestingAuthenticationToken(user, "fake", user.authorities as List)
        def response = authenticationManager.authenticate(testUser)
        SecurityContextHolder.getContext().setAuthentication(response);
    }

}
