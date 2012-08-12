package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup
import com.google.inject.internal.Strings
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.EncryptionKey

public interface ZuulService {

    /* Settings Groups -------------------- */
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName)
    SettingsGroup createSettingsGroupFromPropertiesFile(String name, String env, InputStream inputStream)
    SettingsGroup createSettingsGroupFromCopy(String name, String env, SettingsGroup copy)
    void deleteSettingsGroup(Integer groupId)
    List<SettingsGroup> findSettingsGroupByName(String name)
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
    List<SettingsGroup> listSettingsGroups()
    SettingsGroup save(SettingsGroup group)

    /* Environments -------------------- */
    List<Environment> listEnvironments()

    /* Settings Entry -------------------- */
    SettingsEntry encryptSettingsEntryValue(Integer entryId)
    SettingsEntry decryptSettingsEntryValue(Integer entryId)
    SettingsEntry findSettingsEntry(Integer id)
    void deleteSettingsEntry(Integer entryId)
    SettingsEntry save(SettingsEntry entry)



}