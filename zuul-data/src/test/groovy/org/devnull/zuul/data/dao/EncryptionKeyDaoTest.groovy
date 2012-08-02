package org.devnull.zuul.data.dao

import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EncryptionKeyDaoTest extends ZuulDataIntegrationTest {

    @Autowired
    EncryptionKeyDao dao

    @Test
    void shouldFindKeyByNameAndMapCorrectly() {
        def result = dao.findOne("Default Key")
        assert result.name == "Default Key"
        assert result.description == "Default key which can be used for evaluation purposes.."
        assert result.password == "k(2jsd&01m_.u<"
    }

    @Test
    void shouldContainCorrectNumberOfRecords() {
        assert dao.findAll().size() == 2
    }
}
