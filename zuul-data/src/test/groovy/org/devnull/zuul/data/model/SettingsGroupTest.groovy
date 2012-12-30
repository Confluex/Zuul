package org.devnull.zuul.data.model

import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.junit.Before
import org.junit.Test

public class SettingsGroupTest {
    SettingsGroup group

    @Before
    void createConverter() {
        group = new SettingsGroup(
                id: 1,
                name: "testGroup",
                environment: new Environment(name: "testEnv"),
                key: new EncryptionKey(name: "testKey")
        )
        group.entries.add(new SettingsEntry(key: 'a.b.c', value: 'foo'))
        group.entries.add(new SettingsEntry(key: 'd.e.f', value: 'bar'))
        group.entries.add(new SettingsEntry(key: 'h.i.j', value: 'rot13', encrypted: true))
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
