package com.confluex.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import com.confluex.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Entity
@EqualsAndHashCode(excludes = "groups")
@ToString(includeNames = true, excludes = "groups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Settings implements Serializable {

    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Size(min = 1, message = "Name cannot by empty")
    String name

    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]+", message = "Folders can only contain: numbers, letters, spaces, underscores, dashes")
    @Size(min = 1, max = 32, message = "Folder must be 1-32 characters long")
    String folder

    @OneToMany(mappedBy = "settings", cascade = [CascadeType.ALL])
    List<SettingsGroup> groups = []


    Settings addToGroups(SettingsGroup group) {
        group.settings = this
        groups << group
        return this
    }

    def getAt(key) {
        switch (key?.class) {
            case Environment:
                return groups?.find { it.environment == key}
                break
            default:
                return null
        }
    }


}
