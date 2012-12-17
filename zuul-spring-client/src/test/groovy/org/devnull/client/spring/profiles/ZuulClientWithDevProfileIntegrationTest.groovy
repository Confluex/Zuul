package org.devnull.client.spring.profiles

import org.devnull.client.spring.test.BaseHttpServerIntegrationTest
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ['classpath:test-zuul-profiles-context.xml'])
@ActiveProfiles("dev")
class ZuulClientWithDevProfileIntegrationTest extends BaseHttpServerIntegrationTest {


    @Value("\${jdbc.zuul.url}")
    String url

    @Test
    void shouldInjectUrlForActiveProfile() {
        assert url == "jdbc:h2:file:zuul-dev"
    }
}
