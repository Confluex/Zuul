package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup
import com.google.inject.internal.Strings
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.EncryptionKey

public interface ZuulService {
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName)
    SettingsGroup createSettingsGroupFromPropertiesFile(String name, String env, InputStream inputStream)
    List<SettingsGroup> findSettingsGroupByName(String name)
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
    List<Environment> listEnvironments()
    List<SettingsGroup> listSettingsGroups()
    SettingsEntry encryptSettingsEntryValue(Integer entryId)
    SettingsEntry decryptSettingsEntryValue(Integer entryId)
    SettingsEntry findSettingsEntry(Integer id)
    void deleteSettingsEntry(Integer entryId)
    SettingsEntry save(SettingsEntry entry)
    SettingsGroup save(SettingsGroup group)
}