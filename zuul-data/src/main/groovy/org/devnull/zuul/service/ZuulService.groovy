package org.devnull.zuul.service

import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.security.access.prepost.PreAuthorize

public interface ZuulService {

    /* Settings Groups -------------------- */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsGroup createSettingsGroupFromPropertiesFile(String name, String env, InputStream inputStream)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsGroup createSettingsGroupFromCopy(String name, String env, SettingsGroup copy)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteSettingsGroup(Integer groupId)


    List<SettingsGroup> findSettingsGroupByName(String name)


    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)


    List<SettingsGroup> listSettingsGroups()

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsGroup save(SettingsGroup group)

    /* Environments -------------------- */

    List<Environment> listEnvironments()

    /* Settings Entry -------------------- */

    SettingsEntry findSettingsEntry(Integer id)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsEntry encryptSettingsEntryValue(Integer entryId)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsEntry decryptSettingsEntryValue(Integer entryId)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteSettingsEntry(Integer entryId)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsEntry save(SettingsEntry entry)


}