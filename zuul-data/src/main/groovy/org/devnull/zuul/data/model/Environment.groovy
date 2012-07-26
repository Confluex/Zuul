package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.Id

@Entity
@EqualsAndHashCode(excludes="groups")
@ToString(includeNames = true, excludes="groups")
class Environment {

    @Id
    String name


    @OneToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST], mappedBy="environment")
    List<SettingsGroup> groups = []


}
