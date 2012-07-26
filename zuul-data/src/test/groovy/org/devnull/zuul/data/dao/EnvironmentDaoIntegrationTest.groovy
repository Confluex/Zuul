package org.devnull.zuul.data.dao

import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.devnull.zuul.data.test.ZuulDataIntegrationTest

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
