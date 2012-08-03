package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EncryptionKeyDao
import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsEntryDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsEntry
import org.devnull.zuul.data.model.SettingsGroup
import org.jasypt.util.text.BasicTextEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("zuulService")
@Transactional(readOnly = true)
class ZuulServiceImpl implements ZuulService {

    @Autowired
    EncryptionKeyDao encryptionKeyDao

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    SettingsEntryDao settingsEntryDao

    @Autowired
    EnvironmentDao environmentDao

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

    SettingsEntry encryptSettingsEntryValue(Integer entryId) {
        def entry = settingsEntryDao.findOne(entryId)
        def encryptor = new BasicTextEncryptor();
        encryptor.password = entry.group.key.password
        entry.value = encryptor.encrypt(entry.value)
        entry.encrypted = true
        return settingsEntryDao.save(entry)
    }

    SettingsEntry decryptSettingsEntryValue(Integer entryId) {
        def entry = settingsEntryDao.findOne(entryId)
        def encryptor = new BasicTextEncryptor();
        encryptor.password = entry.group.key.password
        entry.value = encryptor.decrypt(entry.value)
        entry.encrypted = false
        return settingsEntryDao.save(entry)
    }
}
