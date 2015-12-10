package com.confluex.zuul.data.dao

import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.validation.ConstraintViolationException

class EnvironmentDaoIntegrationTest extends ZuulDataIntegrationTest {
    @Autowired
    EnvironmentDao dao

    @Test
    void findOneShouldRetrieveAndMapResults() {
        assert dao.findOne("prod").name == "prod"
        assert dao.findOne("qa").name == "qa"
        assert dao.findOne("dev").name == "dev"
    }

    @Test(expected = ConstraintViolationException)
    void shouldErrorIfNameIsInvalid() {
        def environment = new Environment(name: "a b")
        dao.saveAndFlush(environment)
    }

    @Test
    void shouldSaveAndIncrementRowCount() {
        def count = dao.count()
        def environment = new Environment(name: "another1")
        dao.saveAndFlush(environment)
        assert dao.count() == count + 1
    }
}
