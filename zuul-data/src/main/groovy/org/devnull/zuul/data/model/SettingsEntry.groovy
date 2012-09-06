package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.annotate.JsonBackReference
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.*
import javax.validation.constraints.Size

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
    SettingsGroup group

    @Size(min=1, message= "Key must be at least 1 character long")
    @Column(nullable=false)
    String key

    String value

    @Column(nullable=false)
    Boolean encrypted = false
}
