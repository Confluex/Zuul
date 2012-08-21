package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.error.ConflictingOperationException
import org.jasypt.util.text.BasicTextEncryptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

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


    Lock toggleFlagLock = new ReentrantLock(true)

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
        doWithFlagLock {
            def entry = settingsEntryDao.findOne(entryId)
            if (entry.encrypted) {
                throw new ConflictingOperationException("Cannot encrypt value that are already encrypted. Entry ID: " + entryId)
            }
            log.info("Encrypting entry: key={}", entry.key)
            def encryptor = new BasicTextEncryptor();
            encryptor.password = entry.group.key.password
            entry.value = encryptor.encrypt(entry.value)
            entry.encrypted = true
            return settingsEntryDao.save(entry)
        }
    }

    @Transactional(readOnly = false)
    SettingsEntry decryptSettingsEntryValue(Integer entryId) {
        doWithFlagLock {
            def entry = settingsEntryDao.findOne(entryId)
            if (!entry.encrypted) {
                throw new ConflictingOperationException("Cannot decrypt value that are already decrypted. Entry ID: " + entryId)
            }
            log.info("Decrypting entry: key={}", entry.key)
            def encryptor = new BasicTextEncryptor();
            encryptor.password = entry.group.key.password
            entry.value = encryptor.decrypt(entry.value)
            entry.encrypted = false
            return settingsEntryDao.save(entry)
        }
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

    @Transactional(readOnly=false)
    EncryptionKey saveKey(EncryptionKey key) {
        return encryptionKeyDao.save(key)
    }

    /**
     * Just because I don't trust database blocking transactions
     */
    protected def doWithFlagLock = { closure ->
        try {
            log.debug("Obtaining toggleFlagLock")
            toggleFlagLock.lock()
            log.debug("toggleFlagLock obtained")
            return closure()
        } finally {
            log.debug("Releasing toggleFlagLock")
            toggleFlagLock.unlock()
            log.debug("toggleFlagLock released")
        }
    }


}
