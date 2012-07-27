package org.devnull.zuul.data.model

import org.devnull.zuul.data.config.ZuulDataConstants
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import javax.persistence.Entity
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import org.codehaus.jackson.annotate.JsonBackReference

@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
class SettingsEntry implements Serializable {
    static final long serialVersionUID = ZuulDataConstants.API_VERSION

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name="groupId")
    @JsonBackReference
    SettingsGroup group

    String key
    String value
}
