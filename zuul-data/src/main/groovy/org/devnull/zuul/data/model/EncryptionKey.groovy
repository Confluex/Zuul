package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.Id
import org.devnull.zuul.data.config.ZuulDataConstants

@Entity
@EqualsAndHashCode(includes = "name")
@ToString(includeNames = true, excludes = "password")
class EncryptionKey implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    String name
    String description
    String password
    Boolean defaultKey = false
}
