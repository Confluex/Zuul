package com.confluex.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPUtil
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import com.confluex.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Size

import static com.confluex.zuul.data.config.ZuulDataConstants.*

@Entity
@EqualsAndHashCode(includes = "name")
@ToString(includeNames = true, excludes = "password")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Slf4j
class EncryptionKey implements Serializable {

    @SuppressWarnings("GroovyUnusedDeclaration")
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @Size(min = 1, max = 32)
    @Column(length = 32)
    String name

    @Size(max = 64)
    @Column(length = 64)
    String description

    @Size(min = 8, max = 2000)
    @Column(nullable = false, length = 2000)
    String password

    Boolean defaultKey = false

    @Column(nullable = false, length = 255)
    String algorithm = KEY_ALGORITHM_AES

    Boolean compatibleWith(EncryptionKey otherKey) {
        return this.password == otherKey?.password &&
                this.algorithm == otherKey?.algorithm
    }

    Boolean getIsPgpKey() {
        return PGP_KEY_ALGORITHMS.find { it == algorithm } != null
    }

    Boolean getIsPbeKey() {
        return PBE_KEY_ALGORITHMS.find { it == algorithm} != null
    }

    @AssertTrue(message = "Invalid Public Key")
    Boolean isValidIfPublicKeyAlgorithm() {
        if (isPgpKey) {
            try {
                def publicKey = this as PGPPublicKey
                log.debug("Public Key Fingerprint: {}", publicKey.fingerprint.encodeHex())
            } catch (Exception e) {
                log.warn("Invalid public key", e)
                return false
            }
        }
        return true
    }

    def asType(Class type) {
        switch (type) {
            case EncryptionKey:
                return this
            case PGPPublicKey:
                if (!isPgpKey) {
                    throw new GroovyCastException("${algorithm} is not compatible with PGPPublicKey")
                }
                def ring = new PGPPublicKeyRing(PGPUtil.getDecoderStream(new ByteArrayInputStream(password.bytes)))
                return ring.publicKeys?.find { it.encryptionKey } as PGPPublicKey
            default:
                throw new GroovyCastException("Hmm... ${this.class} cannot be converted to ${type}")
        }
    }
}
