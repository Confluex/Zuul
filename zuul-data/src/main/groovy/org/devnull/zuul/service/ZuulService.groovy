package org.devnull.zuul.service

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.security.access.prepost.PreAuthorize
import org.devnull.security.model.Role

public interface ZuulService {
    /* Notifications ---------------------- */

    /**
     * Send a notification request to system administrators
     * for permissions requests (usually initiated from access
     * denied errors)
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    void notifyPermissionsRequest(String roleName)


    /* Settings Groups -------------------- */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void changeKey(SettingsGroup group, EncryptionKey key)

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

    List<SettingsEntry> search(String query)

    /* Environments -------------------- */

    List<Environment> listEnvironments()

    /**
     * Delete an environment along with any associated settings groups.
     *
     * @param name environment to delete
     */
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    void deleteEnvironment(String name)

    /**
     * Creates a new environment to scope settings groups.
     * @param name unique value
     * @return created entity
     */
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    Environment createEnvironment(String name)

    /* Encryption Keys -------------------- */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<EncryptionKey> listEncryptionKeys()

    /**
     * Update the new key to be the default and set all of the others to false.
     * There can be only one!
     */
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    EncryptionKey changeDefaultKey(String name)

    /**
     * Find the key which is used to encrypt new settings groups by default.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    EncryptionKey findDefaultKey()

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    EncryptionKey findKeyByName(String name)

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    EncryptionKey saveKey(EncryptionKey key)

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    void deleteKey(String name)



}