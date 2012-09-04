package org.devnull.zuul.web.config

import groovy.mock.interceptor.MockFor
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.web.context.support.ServletContextResource
import javax.servlet.ServletContext

import static org.mockito.Mockito.mock

class JstlFunctionsTest {
    @Test
    void shouldFindMavenProjectVersion() {
        def resource = new MockFor(ServletContextResource)
        resource.demand.exists { return true }
        resource.demand.getInputStream { return new ByteArrayInputStream("version=1.2.3".bytes)}
        resource.use {
            assert JstlFunctions.getApplicationVersion(mock(ServletContext)) == "1.2.3"
        }
    }

    @Test
    void shouldFindDevelopmentProjectVersionWhenNotBuiltByMaven() {
        def resource = new MockFor(ClassPathResource)
        resource.demand.exists { return false }
        resource.use {
            assert JstlFunctions.getApplicationVersion(mock(ServletContext)) == "development"
        }
    }
}
