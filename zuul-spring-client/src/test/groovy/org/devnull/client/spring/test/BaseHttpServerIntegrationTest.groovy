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
        server.handler = new ResourceRequestHandler(resource: new ClassPathResource("/mock-server-response.properties"))
        server.start()
    }

    @AfterClass
    static void stopServer() {
        server.stop()
    }

}
