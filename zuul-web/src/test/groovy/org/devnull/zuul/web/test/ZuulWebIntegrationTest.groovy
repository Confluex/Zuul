package org.devnull.zuul.web.test

import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.runner.RunWith


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [
    'classpath:zuul-data-context.xml',
    'classpath:application-context.xml',
    'classpath:security-context.xml'
])
@Transactional('transactionManager')
abstract class ZuulWebIntegrationTest {
}
