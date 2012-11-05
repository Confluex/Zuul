package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.annotate.JsonBackReference
import org.devnull.zuul.data.config.ZuulDataConstants
import org.hibernate.envers.Audited

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.persistence.*

@Audited
@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
class SettingsEntry implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @ManyToOne
    @JoinColumn(name = "groupId")
    @JsonBackReference
    @NotNull
    SettingsGroup group

    @Size(min = 1, message = "Key cannot be empty")
    @NotNull(message = "Key cannot be empty")
    @Column(nullable = false, name = "KEY_NAME")
    String key

    String value

    @Column(nullable = false)
    Boolean encrypted = false
}
