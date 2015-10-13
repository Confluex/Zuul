package com.confluex.zuul.data.specs

import com.confluex.zuul.data.model.SettingsAudit
import org.springframework.data.jpa.domain.Specification

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class SettingsAuditFilter implements Specification<SettingsAudit> {
    Map<String, Serializable> filter

    SettingsAuditFilter(Map<String, Serializable> filter) {
        this.filter = filter
    }

    Predicate toPredicate(Root<SettingsAudit> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        def where = filter.collect { field, value ->
            builder.equal(root.get(field), value)
        } as Predicate[]
        query.where(where)
        return query.restriction
    }
}
