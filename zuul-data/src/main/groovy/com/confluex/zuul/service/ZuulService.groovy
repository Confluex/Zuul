package com.confluex.zuul.service

import com.confluex.util.pagination.Pagination
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.model.Settings
import com.confluex.zuul.data.model.SettingsAudit.AuditType
import com.confluex.zuul.data.model.SettingsEntry
import com.confluex.zuul.data.model.SettingsGroup
import org.springframework.security.access.prepost.PreAuthorize

public interface ZuulService {
    /* Notifications ---------------------- */

    /**
     * Send a notification request to system administrators
     * for permissions requests (usually initiated from access
     * denied errors)
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    void notifyPermissionsRequest(String roleName)

    /* Settings --------------------------- */

    /**
     * Will look for a setttings record with the given name. If not found,
     * it will stub it out with a default record and save it. Implementations
     * should consider concurrency issues.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Settings findOrCreateSettingsByName(String name)

    /**
     * Return all available settings. Implementers should sort by name.
     */
    List<Settings> listSettings()

    /**
     * Lookup a settings record by name. Will return null if it does not exist.
     */
    Settings getSettingsByName(String name)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Settings save(Settings settings)

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    void delete(Settings settings)

    /* Settings Groups -------------------- */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void changeKey(SettingsGroup group, EncryptionKey key)

    @PreAuthorize("hasPermission(#environmentName, 'com.confluex.zuul.data.model.Environment', 'admin')")
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName)

    @PreAuthorize("hasPermission(#environmentName, 'com.confluex.zuul.data.model.Environment', 'admin')")
    SettingsGroup createSettingsGroupFromPropertiesFile(String name, String environmentName, InputStream inputStream)

    @PreAuthorize("hasPermission(#environmentName, 'com.confluex.zuul.data.model.Environment', 'admin')")
    SettingsGroup createSettingsGroupFromCopy(String name, String environmentName, SettingsGroup copy)

    @PreAuthorize("hasPermission(#group.environment, 'admin')")
    void deleteSettingsGroup(SettingsGroup group)

    List<SettingsGroup> findSettingsGroupByName(String name)

    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)

    @PreAuthorize("hasPermission(#group.environment, 'admin')")
    SettingsGroup save(SettingsGroup group)

    /* Settings Entry -------------------- */

    SettingsEntry findSettingsEntry(Integer id)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsEntry encryptSettingsEntryValue(Integer entryId)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    SettingsEntry decryptSettingsEntryValue(Integer entryId)

    @PreAuthorize("hasPermission(#entry.group.environment, 'admin')")
    void deleteSettingsEntry(SettingsEntry entry)

    @PreAuthorize("hasPermission(#entry.group.environment, 'admin')")
    SettingsEntry save(SettingsEntry entry)

    @PreAuthorize("hasPermission(#entry.group.environment, 'admin')")
    SettingsEntry save(SettingsEntry entry, AuditType type)

    @PreAuthorize("hasPermission(#group.environment, 'admin')")
    SettingsEntry createEntry(SettingsGroup group, SettingsEntry entry)

    List<SettingsEntry> search(String query, Pagination<SettingsEntry> pagination)

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


    /**
     * Toggles the environment.restriction flag between true and false
     *
     * @param name environment to toggle
     * @return the newly updated restriction flag's value
     */
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    Boolean toggleEnvironmentRestriction(String name)

    /**
     * Adjust ordinals of the environments which match the names provided
     * with its index in the collection.
     *
     * @param names the ordered list of environment names
     * @return saved and re-ordered environments.
     */
    List<Environment> sortEnvironments(List<String> names)

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