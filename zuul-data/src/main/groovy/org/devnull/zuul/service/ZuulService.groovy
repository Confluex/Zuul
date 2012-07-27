package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup
import com.google.inject.internal.Strings
import org.devnull.zuul.data.model.Environment

public interface ZuulService {
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
    List<Environment> listEnvironments()
}