package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.codehaus.jackson.annotate.JsonBackReference
import org.codehaus.jackson.annotate.JsonIgnore
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.*

@Entity
@EqualsAndHashCode(excludes = 'entries')
@ToString(includeNames = true, excludes = 'entries')
class SettingsGroup implements Serializable {

    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL])
    List<SettingsEntry> entries = []

    @ManyToOne
    @JoinColumn(name = "environment")
    @JsonBackReference
    Environment environment

    @ManyToOne
    @JoinColumn(name = "key")
    @JsonIgnore
    EncryptionKey key

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
                    properties.put(it.key, it.value)
                }
                return properties
            case Map:
                def map = [:]
                map.id = id
                map.name = name
                map.environment = [ name: environment.name ]
                map.key = [ name: key.name, description:key.description ]
                return map
            default:
                throw new GroovyCastException("Hmm... ${this.class} cannot be converted to ${type}")
        }
    }
}
