package org.devnull.zuul.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.zuul.data.dao.SettingsGroupDao
import org.devnull.zuul.data.model.SettingsGroup
import org.devnull.zuul.data.model.Environment

@Service("zuulService")
@Transactional(readOnly=true)
class ZuulServiceImpl implements ZuulService {

    @Autowired
    SettingsGroupDao settingsGroupDao

    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env) {
        def group = settingsGroupDao.findByNameAndEnvironment(name, new Environment(name:env))
        group.entries.size() // lazy-init (TODO: stop being so lazy)
        return group
    }

}
