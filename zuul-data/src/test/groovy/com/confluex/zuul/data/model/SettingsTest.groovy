package com.confluex.zuul.data.model

import org.junit.Before
import org.junit.Test

class SettingsTest {
    Settings settings

    @Before
    void createSettings() {
        settings = new Settings(name: "test-settings")
    }

    @Test
    void shouldSetBiDirectionalRelationshipWhenAddingGroups() {
        def groups = [ new SettingsGroup(id: 1), new SettingsGroup(id: 2)]
        settings.addToGroups(groups[0]).addToGroups(groups[1])
        groups.each {
            assert it.settings == settings
            assert settings.groups.contains(it)
        }
    }

    @Test
    void shouldFindGroupByEnvironment() {
        def dev = new Environment(name: "dev")
        def qa = new Environment(name: "qa")
        def prod = new Environment(name: "prod")
        def groups = [ new SettingsGroup(id: 1, environment: prod), new SettingsGroup(id: 2, environment: dev)]
        def settings = new Settings(groups:groups)

        assert settings[qa] == null
        assert settings[dev] == groups[1]
        assert settings[prod] == groups[0]
        assert settings[null] == null
        assert settings[123] == null
    }
}
