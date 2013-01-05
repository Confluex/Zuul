package org.devnull.zuul.data.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity
@EqualsAndHashCode()
@ToString(includeNames = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class SettingsMixin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(nullable = false, name = "PARENT_GROUP_NAME")
    @NotNull
    String parent

    @Column(nullable = false, name = "TARGET_GROUP_NAME")
    @NotNull
    String target

    Integer ordinal = 0

}
