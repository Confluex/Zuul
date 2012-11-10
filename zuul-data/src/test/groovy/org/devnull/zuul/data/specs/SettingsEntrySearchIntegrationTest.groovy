package org.devnull.zuul.data.specs

import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.model.SettingsEntry

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

    @Test
    void shouldMatchGroupNames() {
        def results = dao.findAll(new SettingsEntrySearch("app-data"))
        assert results.size()  == 13
        results.each {
            it.group.name == "app-data-config"
        }
    }

    @Test
    void shouldMatchGroupEnvironments() {
        def results = dao.findAll(new SettingsEntrySearch("prod"))
        assert results.size()  == 7
        results.each {
            it.group.environment.name == "prod"
        }
    }

    @Test
    void shouldMatchValues() {
        def results = dao.findAll(new SettingsEntrySearch("h2"))
        printResults(results)
        assert results.size()  == 5
        results.each {
            it.value.toLowerCase().contains("h2")
        }
    }

    @Test
    void shouldMatchMultipleTermsAcrossFields() {
        def results = dao.findAll(new SettingsEntrySearch("prod url"))
        results.each {
            println "${it.group.name} - ${it.group.environment} : ${it.key}"
        }
        assert results.size()  == 2
        assert results[0].group.name == "app-data-config"
        assert results[0].group.environment.name== "prod"
        assert results[0].key== "jdbc.zuul.url"

        assert results[1].group.name == "hr-service-config"
        assert results[1].group.environment.name== "prod"
        assert results[1].key== "sftp.bank.url"
    }


    @Test
    void shouldWorkMatchMultipleToSingleField() {
        def results = dao.findAll(new SettingsEntrySearch("zuul jdbc"))
        printResults(results)
        assert results.size()  == 13
        results.each {
            assert it.key.contains("zuul")
            assert it.key.contains("jdbc")
        }
    }

    private void printResults(List<SettingsEntry> results) {
        results.each {
            println "${it.group.name} - ${it.group.environment.name} : ${it.key}=${it.value}"
        }
    }
}
