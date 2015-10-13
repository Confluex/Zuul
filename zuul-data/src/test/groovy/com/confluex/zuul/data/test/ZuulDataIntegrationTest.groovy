package com.confluex.zuul.data.test

import org.junit.runner.RunWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ['classpath:zuul-data-context.xml', 'classpath:test-security-context.xml'])
@Transactional('transactionManager')
@ActiveProfiles(["test", "security-openid"])
abstract class ZuulDataIntegrationTest {

}
