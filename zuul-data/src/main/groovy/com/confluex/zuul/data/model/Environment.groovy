package com.confluex.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import com.confluex.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import javax.persistence.*

@Entity
@EqualsAndHashCode(excludes = "groups")
@ToString(includeNames = true, excludes = "groups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Environment implements Serializable {

    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @Pattern(regexp="^[a-zA-Z0-9]+\$", message="Name can only contain letters and numbers")
    @Size(min=1, max=12, message="Name must be 1-12 characters long")
    @Column(length=12)
    String name


    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "environment")
    List<SettingsGroup> groups = []

    Boolean restricted = false

    Integer ordinal = 0


}
