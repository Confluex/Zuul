package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Size
import javax.persistence.Cacheable
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
@EqualsAndHashCode(includes = "name")
@ToString(includeNames = true, excludes = "password")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
class EncryptionKey implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @Size(min = 1, max = 32)
    @Column(length = 32)
    String name

    @Size(max = 64)
    @Column(length = 64)
    String description

    @Size(min = 8, max = 32)
    @Column(nullable = false, length = 32)
    String password

    Boolean defaultKey = false
}
