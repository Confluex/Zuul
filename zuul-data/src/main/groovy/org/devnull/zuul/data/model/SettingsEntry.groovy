package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.annotate.JsonBackReference
import org.devnull.zuul.data.config.ZuulDataConstants

import javax.persistence.*
import javax.validation.constraints.Size
import org.hibernate.annotations.Index
import javax.validation.constraints.NotNull
import org.hibernate.envers.Audited

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
    @Index(name="Idx_Settings_Entry_Group")
    @NotNull
    SettingsGroup group

    @Size(min=1, message= "Key cannot be empty")
    @NotNull(message= "Key cannot be empty")
    @Column(nullable=false, name="KEY_NAME")
    String key

    String value

    @Column(nullable=false)
    Boolean encrypted = false
}
