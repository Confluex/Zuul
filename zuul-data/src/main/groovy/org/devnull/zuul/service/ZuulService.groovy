package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup
import com.google.inject.internal.Strings
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry

public interface ZuulService {
    List<SettingsGroup> findSettingsGroupByName(String name)
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
    List<Environment> listEnvironments()
    List<SettingsGroup> listSettingsGroups()
    SettingsEntry encryptSettingsEntryValue(Integer entryId)
    SettingsEntry decryptSettingsEntryValue(Integer entryId)
    SettingsEntry findSettingsEntry(Integer id)
}