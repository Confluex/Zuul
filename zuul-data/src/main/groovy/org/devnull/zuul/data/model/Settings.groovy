package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Size(min = 1, message = "Name cannot by empty")
    String name

    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]+", message = "Folders can only contain: numbers, letters, spaces, underscores, dashes")
    @Size(min = 1, max = 32, message = "Folder must be 1-32 characters long")
    @NotNull
    String folder

    @OneToMany(mappedBy = "settings", cascade = [CascadeType.ALL])
    @javax.persistence.OrderBy("environment.ordinal")
    List<SettingsGroup> groups = []


    Settings addToGroups(SettingsGroup group) {
        group.settings = this
        groups << group
        return this
    }
}
