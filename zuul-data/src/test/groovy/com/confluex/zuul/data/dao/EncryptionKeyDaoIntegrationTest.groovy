package com.confluex.zuul.data.dao

import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EncryptionKeyDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    EncryptionKeyDao dao

    @Test
    void shouldFindKeyByNameAndMapCorrectly() {
        def result = dao.findOne("Default Key")
        assert result.name == "Default Key"
        assert result.description == "Default key which can be used for evaluation purposes.."
        assert result.password == "badpassword1"
        assert result.defaultKey
    }

    @Test
    void shouldContainCorrectNumberOfRecords() {
        assert dao.findAll().size() == 2
    }
}
