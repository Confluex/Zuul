package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.specs.SettingsEntryEncryptedWithKey
import org.devnull.zuul.service.error.ConflictingOperationException
import org.devnull.zuul.service.security.EncryptionStrategy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.mail.MailSender
import org.devnull.security.service.SecurityService
import org.devnull.zuul.data.config.ZuulDataConstants
import org.springframework.mail.SimpleMailMessage
import org.devnull.security.model.Role

@Service("zuulService")
@Transactional(readOnly = true)
class ZuulServiceImpl implements ZuulService {

    final def log = LoggerFactory.getLogger(this.class)

    @Autowired
    EncryptionKeyDao encryptionKeyDao

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    SettingsEntryDao settingsEntryDao

    @Autowired
    EnvironmentDao environmentDao

    @Autowired
    EncryptionStrategy encryptionStrategy

    @Autowired
    MailSender mailSender

    @Autowired
    SimpleMailMessage templateMessage

    @Autowired
    SecurityService securityService

    @Transactional(readOnly = false)
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName) {
        log.info("Creating empty group for name: {}, environment: {}", groupName, environmentName)
        def env = environmentDao.findOne(environmentName)
        def key = findDefaultKey()
        def group = new SettingsGroup(name: groupName, environment: env, key: key)
        return settingsGroupDao.save(group)
    }

    @Transactional(readOnly = false)
    SettingsGroup createSettingsGroupFromPropertiesFile(String groupName, String environmentName, InputStream inputStream) {
        def group = createEmptySettingsGroup(groupName, environmentName)
        log.info("Appending entries from properties file..")
        def properties = new Properties()
        properties.load(inputStream)
        log.info("Loading entries: {}", properties)
        properties.each {k, v ->
            group.addToEntries(new SettingsEntry(key: k, value: v))
        }
        return settingsGroupDao.save(group)
    }

    @Transactional(readOnly = false)
    SettingsGroup createSettingsGroupFromCopy(String name, String environmentName, SettingsGroup copy) {
        log.info("Creating copy for name:{}, env:{} from: {}", name, environmentName, copy)
        def env = environmentDao.findOne(environmentName)
        def group = new SettingsGroup(name: name, environment: env, key: copy.key)
        copy.entries.each {
            def entry = new SettingsEntry(key: it.key, value: it.value, encrypted: it.encrypted)
            log.info("appending entry: {}", entry)
            group.addToEntries(entry)
        }
        return settingsGroupDao.save(group)
    }

    @Transactional(readOnly = false)
    void deleteSettingsGroup(Integer groupId) {
        log.info("Deleteing settings group: {} ", groupId)
        settingsGroupDao.delete(groupId)
    }

    List<SettingsGroup> findSettingsGroupByName(String name) {
        return settingsGroupDao.findByName(name)
    }

    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env) {
        log.debug("Finding settings group by name:{}, env:{}", name, env)
        return settingsGroupDao.findByNameAndEnvironment(name, new Environment(name: env))
    }

    List<Environment> listEnvironments() {
        return environmentDao.findAll() as List<Environment>
    }

    List<SettingsGroup> listSettingsGroups() {
        return settingsGroupDao.findAll(new Sort("name")) as List<SettingsGroup>
    }

    @Transactional(readOnly = false)
    SettingsEntry encryptSettingsEntryValue(Integer entryId) {
        def entry = settingsEntryDao.findOne(entryId)
        if (entry.encrypted) {
            throw new ConflictingOperationException("Cannot encrypt value that are already encrypted. Entry ID: " + entryId)
        }
        entry.value = encryptionStrategy.encrypt(entry.value, entry.group.key)
        entry.encrypted = true
        return settingsEntryDao.save(entry)
    }

    @Transactional(readOnly = false)
    SettingsEntry decryptSettingsEntryValue(Integer entryId) {
        def entry = settingsEntryDao.findOne(entryId)
        if (!entry.encrypted) {
            throw new ConflictingOperationException("Cannot decrypt value that are already decrypted. Entry ID: " + entryId)
        }
        entry.value = encryptionStrategy.decrypt(entry.value, entry.group.key)
        entry.encrypted = false
        return settingsEntryDao.save(entry)
    }

    SettingsEntry findSettingsEntry(Integer id) {
        return settingsEntryDao.findOne(id)
    }

    @Transactional(readOnly = false)
    void deleteSettingsEntry(Integer entryId) {
        log.info("Deleteing entry: {}", entryId)
        settingsEntryDao.delete(entryId)
    }

    @Transactional(readOnly = false)
    SettingsEntry save(SettingsEntry entry) {
        log.info("Saving entry: {}", entry)
        return settingsEntryDao.save(entry)
    }

    @Transactional(readOnly = false)
    SettingsGroup save(SettingsGroup group) {
        log.info("Saving group: {}", group)
        return settingsGroupDao.save(group)
    }

    void notifyPermissionsRequest(Role requested) {
        def requester = securityService.currentUser
        def sysAdmins = securityService.findRoleByName(ZuulDataConstants.ROLE_SYSTEM_ADMIN)
        def emails = sysAdmins.users.collect { it.email } as String[]
        if (emails) {
            def message = new SimpleMailMessage(templateMessage);
            message.to = emails
            message.cc = [requester.email]
            message.subject = "Request for permissions: ${requester.firstName} ${requester.lastName}"
            message.text = "${requester.firstName} ${requester.lastName} has requested access to role: ${requested.description}"
            mailSender.send(message)
        }
    }

    @Transactional(readOnly = false)
    void changeKey(SettingsGroup group, EncryptionKey newKey) {
        def existingKey = group.key
        group.entries.each {
            if (it.encrypted) {
                def decrypted = encryptionStrategy.decrypt(it.value, existingKey)
                it.value = encryptionStrategy.encrypt(decrypted, newKey)
            }
        }
        group.key = newKey
        settingsGroupDao.save(group)
    }

    List<EncryptionKey> listEncryptionKeys() {
        return encryptionKeyDao.findAll(new Sort("name")) as List
    }

    @Transactional(readOnly = false)
    EncryptionKey changeDefaultKey(String name) {
        def newKey = encryptionKeyDao.findOne(name)
        if (newKey.defaultKey) {
            return newKey
        }
        def oldKey = findDefaultKey()
        newKey.defaultKey = true
        oldKey.defaultKey = false
        encryptionKeyDao.save([newKey, oldKey])
        return newKey
    }

    EncryptionKey findDefaultKey() {
        def key = encryptionKeyDao.findAll().find { it.defaultKey }
        log.info("Found default encryption key: {}", key)
        //noinspection GroovyAssignabilityCheck
        return key
    }

    EncryptionKey findKeyByName(String name) {
        return encryptionKeyDao.findOne(name)
    }

    @Transactional(readOnly = false)
    EncryptionKey saveKey(EncryptionKey key) {
        def existingKey = encryptionKeyDao.findOne(key.name)
        log.debug("{} != {} : {}", existingKey.password, key.password, existingKey.password != key.password)
        if (existingKey.password != key.password) {
            reEncryptEntriesWithMatchingKey(existingKey, key)
        }
        return encryptionKeyDao.save(key)
    }


    void deleteKey(String name) {
        def key = encryptionKeyDao.findOne(name)
        if (key.defaultKey) {
            throw new ConflictingOperationException("Cannot delete default key")
        }
        def existingGroups = settingsGroupDao.findByKey(key)
        def defaultKey = findDefaultKey()
        existingGroups.each { group ->
            changeKey(group, defaultKey)
        }
        encryptionKeyDao.delete(name)
    }


    protected void reEncryptEntriesWithMatchingKey(EncryptionKey existingKey, EncryptionKey newKey) {
        settingsEntryDao.findAll(new SettingsEntryEncryptedWithKey(existingKey)).each { entry ->
            log.info("re-encrypting entry:{}, oldKey:{}, newKey:{}", entry, existingKey, newKey)
            def decrypted = encryptionStrategy.decrypt(entry.value, existingKey)
            entry.value = encryptionStrategy.encrypt(decrypted, newKey)
            settingsEntryDao.save(entry)
        }
    }


}
