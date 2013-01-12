package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.devnull.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Size

import static org.devnull.zuul.data.config.ZuulDataConstants.*

@Entity
@EqualsAndHashCode(includes = "name")
@ToString(includeNames = true, excludes = "password")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class EncryptionKey implements Serializable {
    static final Map configurations = [
            "PBEWITHSHA256AND256BITAES-CBC-BC": [
                    description: "AES cipher with 256"
            ]
    ]
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

    @Column(nullable = false, length = 255)
    String algorithm = KEY_ALGORITHM_AES

    Boolean compatibleWith(EncryptionKey otherKey) {
        return this.password == otherKey?.password &&
                this.algorithm == otherKey?.algorithm
    }

    Boolean isPgpKey() {
        return PGP_KEY_ALGORITHMS.find { it == algorithm } != null
    }

    Boolean isPbeKey() {
        return PBE_KEY_ALGORITHMS.find { it == algorithm} != null
    }
}
