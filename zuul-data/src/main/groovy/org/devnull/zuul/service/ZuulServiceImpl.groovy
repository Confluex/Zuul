package org.devnull.zuul.service

import org.devnull.zuul.data.dao.EnvironmentDao
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("zuulService")
@Transactional(readOnly = true)
class ZuulServiceImpl implements ZuulService {

    @Autowired
    SettingsGroupDao settingsGroupDao

    @Autowired
    EnvironmentDao environmentDao

    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env) {
        def group = settingsGroupDao.findByNameAndEnvironment(name, new Environment(name: env))
        group.entries.size() // lazy-init (TODO: stop being so lazy)
        return group
    }

    List<Environment> listEnvironments() {
        return environmentDao.findAll() as List<Environment>
    }
}
