package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup

public interface ZuulService {
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
}