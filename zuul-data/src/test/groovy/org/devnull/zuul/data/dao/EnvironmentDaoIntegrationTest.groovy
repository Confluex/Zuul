package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EnvironmentDaoIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EnvironmentDao dao

    @Test
    void findOneShouldRetrieveAndMapResults() {
        assert dao.findOne("prod").name == "prod"
        assert dao.findOne("qa").name == "qa"
        assert dao.findOne("dev").name == "dev"
    }
}
