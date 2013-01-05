package org.devnull.zuul.data.dao

import org.devnull.zuul.data.model.SettingsMixin
import org.springframework.data.jpa.repository.JpaRepository

public interface SettingsMixinDao extends JpaRepository<SettingsMixin, Integer> {

}