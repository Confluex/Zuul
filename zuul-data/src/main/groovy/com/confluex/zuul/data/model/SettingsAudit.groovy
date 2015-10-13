package com.confluex.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import com.confluex.zuul.data.config.ZuulDataConstants

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
    String groupEnvironment
    String modifiedBy
    Date modifiedDate
    AuditType type

    enum AuditType {
        ADD("Add", "added"), MOD("Modify", "modified"), DELETE("Delete", "deleted"),
        ENCRYPT("Encrypt", "encrypted"),DECRYPT("Decrypt", "decrypted")

        String label
        String action
        AuditType(String label, String action) {
            this.label = label
            this.action = action
        }
        String toString() {
            return label
        }
    }
}

