package org.devnull.zuul.data.converter

import org.junit.Before
import org.junit.Test;

public class PropertiesToSettingsGroupConverterTest {
    PropertiesToSettingsGroupConverter converter

    @Before
    void createConverter() {
        converter = new PropertiesToSettingsGroupConverter()
    }

    @Test
    void shouldConvertPropertiesToSettingsWithTheCorrectKvps() {
        def props = new Properties()
        props['a.b.c'] = 'foo'
        props['d.e.f'] = 'bar'
        def group = converter.convert(props)
        assert group.entries.size() == 2
        assert group.entries[0].key == 'a.b.c'
        assert group.entries[0].value == 'foo'
        assert group.entries[1].key == 'd.e.f'
        assert group.entries[1].value == 'bar'
    }
}
