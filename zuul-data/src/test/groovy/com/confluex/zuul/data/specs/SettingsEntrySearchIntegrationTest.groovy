package com.confluex.zuul.data.specs

import com.confluex.zuul.data.dao.SettingsEntryDao
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsEntrySearchIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsEntryDao dao

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

    @Test
    void shouldMatchEntryKeys() {
        def results = dao.findAll(new SettingsEntrySearch("sftp bank"))
        assert results.size() == 3
        assert results[0].key == "sftp.bank.url"
        assert results[1].key == "sftp.bank.username"
        assert results[2].key == "sftp.bank.password"
    }

    @Test
    void shouldMatchValues() {
        def results = dao.findAll(new SettingsEntrySearch("h2"))
        printResults(results)
        assert results.size() == 5
        results.each {
            it.value.toLowerCase().contains("h2")
        }
    }

    @Test
    void shouldMatchMultipleTermsAcrossFields() {
        def results = dao.findAll(new SettingsEntrySearch("username sa"))
        printResults(results)
        assert results.size() == 1
        assert results[0].key == "jdbc.zuul.username"
        assert results[0].value == "sa"
    }



    protected void printResults(List<SettingsEntry> results) {
        results.each {
            println "${it.group.name} - ${it.group.environment.name} : ${it.key}=${it.value}"
        }
    }
}
