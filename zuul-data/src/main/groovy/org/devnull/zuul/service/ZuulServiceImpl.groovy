package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.service.error.ConflictingOperationException
import org.jasypt.util.text.BasicTextEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import org.slf4j.LoggerFactory
import org.devnull.zuul.data.model.EncryptionKey

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

    @Transactional(readOnly=false)
    SettingsGroup createEmptySettingsGroup(String groupName, String environmentName) {
        def env = environmentDao.findOne(environmentName)
        def key = findDefaultKey()
        def group = new SettingsGroup(name:groupName, environment: env, key: key)
        return settingsGroupDao.save(group)
    }

    List<SettingsGroup> findSettingsGroupByName(String name) {
        return settingsGroupDao.findByName(name)
    }

    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env) {
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

    @Transactional(readOnly=false)
    void deleteSettingsEntry(Integer entryId) {
        settingsEntryDao.delete(entryId)
    }

    @Transactional(readOnly=false)
    SettingsEntry save(SettingsEntry entry) {
        return settingsEntryDao.save(entry)
    }

    protected def doWithFlagLock = { closure ->
        try {
            log.info("Obtaining toggleFlagLock")
            toggleFlagLock.lock()
            log.info("toggleFlagLock obtained")
            return closure()
        } finally {
            log.info("Releasing toggleFlagLock")
            toggleFlagLock.unlock()
            log.info("toggleFlagLock released")
        }
    }

    protected EncryptionKey findDefaultKey() {
        //noinspection GroovyAssignabilityCheck
        return encryptionKeyDao.findAll().find { it.defaultKey }
    }
}
