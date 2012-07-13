package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test

class ApplicationDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    ApplicationDao applicationDao

    @Test
    void findOneShouldRetrieveCorrectRecord() {
        def result = applicationDao.findOne(1)
        assert result.id == 1
    }
}
