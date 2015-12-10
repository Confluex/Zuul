package com.confluex.zuul.data.model

import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.junit.Before
import org.junit.Test

public class SettingsGroupTest {
    SettingsGroup group

    @Before
    void createGroup() {
        group = new SettingsGroup(
                id: 1,
                settings: new Settings(name: "testGroup"),
                environment: new Environment(name: "testEnv"),
                key: new EncryptionKey(name: "testKey")
        )
        group.entries.add(new SettingsEntry(key: 'a.b.c', value: 'foo'))
        group.entries.add(new SettingsEntry(key: 'd.e.f', value: 'bar'))
        group.entries.add(new SettingsEntry(key: 'h.i.j', value: 'rot13', encrypted: true))
    }


    @Test
    void shouldSetParentSettingsName() {
        def group = new SettingsGroup(settings: new Settings(name: "oldGroup"))
        assert group.name == "oldGroup"
        group.name = "newGroup"
        assert group.settings.name == "newGroup"
        assert group.name == "newGroup"
    }

    @Test
    void shouldConvertToPropertiesWithCorrectKeyVals() {
        def props = group as Properties
        assert props.size() == 3
        assert props['a.b.c'] == 'foo'
        assert props['d.e.f'] == 'bar'
        assert props['h.i.j'] == 'ENC(rot13)'
    }

    @Test
    void shouldConvertToMap() {
        def map = group as Map
        assert map['a.b.c'] == 'foo'
        assert map['d.e.f'] == 'bar'
        assert map['h.i.j'] == 'ENC(rot13)'
    }

    @Test
    void shouldConvertNullValuesToEmptyStringWhenConvertingToProperties() {
        group.entries[0].value = null
        def properties = group as Properties
        assert properties[group.entries[0].key] == ''
    }

    @Test
    void shouldConvertNullValuesToEmptyStringWhenConvertingToMap() {
        group.entries[0].value = null
        def result = group as Map
        assert result[group.entries[0].key] == ''
    }

    @Test
    void shouldSetBiDirectionalRelationshipWhenAddingEntries() {
        def entry = new SettingsEntry(key: "testkey", value: "testval")
        group.addToEntries(entry)
        assert entry.group == group
        assert group.entries.contains(entry)
    }

    @Test(expected = GroovyCastException)
    void shouldThrowExceptionWhenCastToInvalidType() {
        group as List
    }


}
