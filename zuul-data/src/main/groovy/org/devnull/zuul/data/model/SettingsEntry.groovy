package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.annotate.JsonBackReference
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.TableGenerator
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
class SettingsEntry implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @TableGenerator(name = "SETTINGS_ENTRY_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "SETTINGS_ENTRY_SEQ",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SETTINGS_ENTRY_GEN")
    Integer id

    @ManyToOne
    @JoinColumn(name = "groupId")
    @JsonBackReference("settingsEntry.group")
    @NotNull
    SettingsGroup group

    @Size(min = 1, message = "Key cannot be empty")
    @NotNull(message = "Key cannot be empty")
    @Column(nullable = false, name = "KEY_NAME")
    String key

    String value

    @Column(nullable = false)
    Boolean encrypted = false
}
