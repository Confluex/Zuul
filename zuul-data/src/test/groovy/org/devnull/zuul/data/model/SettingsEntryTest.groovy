package org.devnull.zuul.data.model

import org.junit.Test


class SettingsEntryTest {

    @Test
    void shouldCopyValuesToNewObjectReference() {
        def group = new SettingsGroup(key: new EncryptionKey(password: "abc123"))
        def entry = new SettingsEntry(id: 1, key: "a", value: "foo", encrypted: true)
        group.addToEntries(entry)
        def copy = entry.copy()
        assert !copy.is(entry)
        assert copy == entry
    }
}
