package org.devnull.zuul.data.model

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
}
