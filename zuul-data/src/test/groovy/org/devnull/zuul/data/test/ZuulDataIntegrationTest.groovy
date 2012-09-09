package org.devnull.zuul.data.test

import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ['classpath:zuul-data-context.xml', 'classpath:test-security-context.xml'])
@Transactional('transactionManager')
abstract class ZuulDataIntegrationTest {

    @BeforeClass
    static void setDataConfigLocation() {
        System.setProperty('zuul.data.config', 'classpath:sample-data-config.properties')
    }
}
