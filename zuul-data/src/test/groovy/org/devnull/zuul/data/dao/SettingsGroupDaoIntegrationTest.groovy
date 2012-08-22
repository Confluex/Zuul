package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.test.ZuulDataIntegrationTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsGroupDaoIntegrationTest extends ZuulDataIntegrationTest {

    @Autowired
    SettingsGroupDao dao

    @Autowired
    SettingsEntryDao entryDao

    @Test
    void findByNameAndEnvironmentShouldFindRecordAndMapCorrectly() {
        def group = dao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        assert group.id == 1
        assert group.name == "app-data-config"
        assert group.environment.name == "dev"
        assert group.entries.size() == 7
        assert group.key.name == "Default Key"

        assert group.entries[0].key == "jdbc.zuul.url"
        assert group.entries[0].value == "jdbc:h2:mem:zuul"

        assert group.entries[1].key == "jdbc.zuul.generate.ddl"
        assert group.entries[1].value == "create-drop"

        assert group.entries[2].key == "jdbc.zuul.username"
        assert group.entries[2].value == "sa"

        assert group.entries[3].key == "jdbc.zuul.password"
        assert group.entries[3].value == ""

        assert group.entries[4].key == "jdbc.zuul.driver"
        assert group.entries[4].value == "org.h2.Driver"

        assert group.entries[5].key == "jdbc.zuul.dialect"
        assert group.entries[5].value == "org.hibernate.dialect.H2Dialect"

        assert group.entries[6].key == "jdbc.zuul.validationQuery"
        assert group.entries[6].value == "select 1"
    }

    @Test
    void findByNameShouldReturnCorrectNumberOfRecords() {
        def groups = dao.findByName("app-data-config")
        assert groups.size() == 3
        groups.each { assert it.name == "app-data-config" }
        assert groups.find { it.environment.name == "dev" }
        assert groups.find { it.environment.name == "qa" }
        assert groups.find { it.environment.name == "prod" }
    }

    @Test
    void shouldCascadePersistNewEntries() {
        def group = dao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        assert group.entries.size() == 7
        group.addToEntries(new SettingsEntry(key: "new.key", value: "woohoo!"))
        dao.save(group)
        def result = dao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        assert result.entries.size() == 8
        assert result.entries.last().key == "new.key"
        assert result.entries.last().value == "woohoo!"
    }

    @Test
    void shouldCascadeDeleteEntries() {
        def count = entryDao.count()
        def group = dao.findByNameAndEnvironment("app-data-config", new Environment(name: "dev"))
        def decrementBy = group.entries.size()
        dao.delete(group.id)
        assert entryDao.count() == count - decrementBy
    }

    @Test
    void shouldFindGroupsByKey() {
        def results = dao.findByKey(new EncryptionKey(name: "Default Key"))
        assert results.size() == 3
        results.each {
            assert it.key.name == "Default Key"
        }
    }

    @Test
    void shouldReturnNoResultsWhenFindingGroupsWithoutAMatchingKey() {
        def results = dao.findByKey(new EncryptionKey(name: "Fake Key"))
        assert !results
    }
}
