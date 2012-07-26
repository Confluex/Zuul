package org.devnull.zuul.service

import org.devnull.zuul.data.model.SettingsGroup
import com.google.inject.internal.Strings

public interface ZuulService {
    SettingsGroup findSettingsGroupByNameAndEnvironment(String name, String env)
}