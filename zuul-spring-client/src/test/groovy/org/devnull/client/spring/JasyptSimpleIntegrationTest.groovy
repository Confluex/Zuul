package org.devnull.client.spring

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.Value
import org.junit.Before
import org.junit.Ignore
import org.devnull.client.spring.test.BaseHttpServerIntegrationTest

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ 'classpath:test-jasypt-client-context.xml' ])
class JasyptSimpleIntegrationTest extends BaseHttpServerIntegrationTest {

    @Value("\${jdbc.zuul.password}")
    String password

    @Test
    void shouldInjectDecryptedValue() {
        assert password == "supersecure"
    }
}
