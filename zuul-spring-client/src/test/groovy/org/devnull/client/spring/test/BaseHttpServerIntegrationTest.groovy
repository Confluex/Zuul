package org.devnull.client.spring.test

import org.junit.AfterClass
import org.junit.BeforeClass
import org.mortbay.jetty.Server
import org.springframework.core.io.ClassPathResource
import org.devnull.client.spring.ZuulPropertiesFactoryBean

abstract class BaseHttpServerIntegrationTest {
    static Server server

    @BeforeClass
    static void createServer() {
        server = new Server(8081)
        def resources = [
                "/zuul/settings/prod/app-data-config.properties" :new ClassPathResource("/mock-server-response.properties")
        ]
        server.handler = new ResourceRequestHandler(resources: resources)
        server.start()
    }

    @AfterClass
    static void stopServer() {
        server.stop()
    }

}
