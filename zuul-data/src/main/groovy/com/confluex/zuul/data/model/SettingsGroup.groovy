package com.confluex.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.codehaus.jackson.annotate.JsonBackReference
import org.codehaus.jackson.annotate.JsonIgnore
import com.confluex.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.validation.constraints.NotNull

@Entity
@EqualsAndHashCode(excludes = 'entries')
@ToString(includeNames = true, excludes = 'entries')
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class SettingsGroup implements Serializable {

    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL])
    @OrderBy("key")
    List<SettingsEntry> entries = []

    @ManyToOne(optional = false)
    @JoinColumn(name = "environment")
    @JsonBackReference("settingsGroup.environment")
    @NotNull
    Environment environment

    @ManyToOne(optional = false)
    @JoinColumn(name = "key_name")
    @JsonIgnore
    @NotNull
    EncryptionKey key

    @ManyToOne(optional = false)
    @JoinColumn(name = "settings_id")
    @JsonBackReference("settingsGroup.settings")
    @NotNull
    Settings settings

    /**
     * Name is a now a shortcut to the setting object's name.
     */
    String getName() {
        return settings.name
    }

    /**
     * Name is a now a shortcut to the setting object's name
     */
    void setName(String name) {
        settings.name = name
    }

    SettingsGroup addToEntries(SettingsEntry entry) {
        entry.group = this
        entries << entry
        return this
    }

    def asType(Class type) {
        switch (type) {
            case SettingsGroup:
                return this
            case Properties:
                def properties = new Properties()
                entries.each {
                    def value = it.value ?: ''
                    properties.put(it.key, it.encrypted ? "ENC(${value})".toString() : value)
                }
                return properties
            case Map:
                def map = [:]
                entries.each {
                    def value = it.value ?: ''
                    map[it.key] = it.encrypted ? "ENC(${value})".toString() : value
                }
                return map
            default:
                throw new GroovyCastException("Hmm... ${this.class} cannot be converted to ${type}")
        }
    }
}
