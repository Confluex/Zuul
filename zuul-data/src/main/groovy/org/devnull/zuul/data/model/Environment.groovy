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
import org.devnull.zuul.data.config.ZuulDataConstants

@Entity
@EqualsAndHashCode(includes="name")
@ToString(includeNames = true, excludes="groups")
class Environment implements Serializable {

    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    String name


    @OneToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST], mappedBy="environment")
    List<SettingsGroup> groups = []


}
