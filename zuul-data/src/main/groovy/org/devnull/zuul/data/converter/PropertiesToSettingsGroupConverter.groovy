package org.devnull.zuul.data.converter

import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.core.convert.converter.Converter
import org.devnull.zuul.data.model.SettingsEntry

class PropertiesToSettingsGroupConverter implements Converter<Properties, SettingsGroup> {

    SettingsGroup convert(Properties source) {
        def group = new SettingsGroup()
        group.entries = source.propertyNames().collect { name ->
            new SettingsEntry(key:name, value:source.getProperty(name))
        }
        return group
    }

}
