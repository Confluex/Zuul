package org.devnull.client.spring

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.Value
import org.junit.Before
import org.junit.Ignore

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ 'classpath:test-jasypt-client-context.xml' ])
class JasyptSimpleIntegrationTest {

    @Value("\${jdbc.zuul.password}")
    String password

    @Test
    @Ignore("Manual test only.. zuul webapp needs to be running on localhost")
    void shouldInjectDecryptedValue() {
        assert password == "supersecure"
    }
}
