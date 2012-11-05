package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.codehaus.jackson.annotate.JsonBackReference
import org.codehaus.jackson.annotate.JsonIgnore
import org.devnull.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Index
import org.hibernate.envers.Audited

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.persistence.*

import static org.hibernate.envers.RelationTargetAuditMode.*

@Audited
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
    @JsonBackReference
    @NotNull
    @Audited(targetAuditMode = NOT_AUDITED)
    Environment environment

    @ManyToOne(optional = false)
    @JoinColumn(name = "key_name")
    @JsonIgnore
    @NotNull
    @Audited(targetAuditMode = NOT_AUDITED)
    EncryptionKey key

    @Size(min = 1, message = "Name cannot by empty")
    @Column(nullable = false)
    @Index(name = "Idx_Settings_Entry_Name")
    String name

    void addToEntries(SettingsEntry entry) {
        entry.group = this
        entries << entry
    }

    def asType(Class type) {
        switch (type) {
            case SettingsGroup:
                return this
            case Properties:
                def properties = new Properties()
                entries.each {
                    properties.put(it.key, it.encrypted ? "ENC(${it.value})".toString() : it.value)
                }
                return properties
            case Map:
                def map = [:]
                map.id = id
                map.name = name
                map.environment = [name: environment.name]
                map.key = [name: key.name, description: key.description]
                return map
            default:
                throw new GroovyCastException("Hmm... ${this.class} cannot be converted to ${type}")
        }
    }
}
