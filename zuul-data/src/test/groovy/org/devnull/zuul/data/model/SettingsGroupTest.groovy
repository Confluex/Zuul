package org.devnull.zuul.data.model

import org.junit.Before
import org.junit.Test
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

public class SettingsGroupTest {
    SettingsGroup group

    @Before
    void createConverter() {
        group = new SettingsGroup()
        group.entries.add(new SettingsEntry(key: 'a.b.c', value: 'foo'))
        group.entries.add(new SettingsEntry(key: 'd.e.f', value: 'bar'))
    }

    @Test
    void shouldConvertToPropertiesWithCorrectKeyVals() {
        def props = group as Properties
        assert props.size() == 2
        assert props['a.b.c'] == 'foo'
        assert props['d.e.f'] == 'bar'
    }

    @Test
    void shouldSetBiDirectionalRelationshipWhenAddingEntries() {
        def entry = new SettingsEntry(key: "testkey", value: "testval")
        group.addToEntries(entry)
        assert entry.group == group
        assert group.entries.contains(entry)
    }
    
    @Test(expected=GroovyCastException)
    void shouldThrowExceptionWhenCastToInvalidType() {
        def props = group as Map

    }
}
