package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.annotate.JsonBackReference
import org.devnull.security.model.User
import org.devnull.zuul.data.config.ZuulDataConstants
import org.hibernate.annotations.Index
import org.hibernate.envers.Audited
import org.springframework.data.jpa.domain.AbstractAuditable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Audited
@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
class SettingsEntry extends AbstractAuditable<User, Integer> {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @ManyToOne
    @JoinColumn(name = "groupId")
    @JsonBackReference
    @Index(name = "Idx_Settings_Entry_Group")
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
