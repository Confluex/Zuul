package org.devnull.zuul.data.specs

import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsEntrySearchIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

    @Test
    void shouldMatchEntryKeys() {
        def results = dao.findAll(new SettingsEntrySearch("sftp bank"))
        assert results.size() == 3
        assert results[0].key == "sftp.bank.url"
        assert results[1].key == "sftp.bank.username"
        assert results[2].key == "sftp.bank.password"
    }


    @Test
    void shouldFindEmptyResultsWhenNoTermsAreMatched() {
        def results = dao.findAll(new SettingsEntrySearch("notfound"))
        assert results.size() == 0
    }

    @Test
    void shouldFindAllResultsWhenNoTermsArePassed() {
        def results = dao.findAll(new SettingsEntrySearch(null))
        assert results.size() == dao.count()
    }
}
