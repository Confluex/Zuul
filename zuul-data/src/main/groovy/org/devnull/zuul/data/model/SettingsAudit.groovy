package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
class SettingsAudit implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String settingsKey
    String settingsValue
    Boolean encrypted
    String groupName
    String modifiedBy
    Date modifiedDate
    AuditType type

    enum AuditType {
        DELETE, ADD, MOD
    }
}

