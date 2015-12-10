package com.confluex.zuul.data.specs

import com.confluex.zuul.data.model.SettingsEntry
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.CriteriaBuilder
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import javax.persistence.criteria.JoinType

@ToString(includeNames=true)
@EqualsAndHashCode
class SettingsEntrySearch  implements Specification<SettingsEntry> {

    String query

    SettingsEntrySearch(String query) {
        this.query = query
    }

    Predicate toPredicate(Root<SettingsEntry> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        def terms = buildSearchTerms()
        def where = terms.collect { term ->
            def valuesPredicates = builder.like(builder.lower(root.get("value")), "%${term}%")
            def keysPredicates = builder.like(builder.lower(root.get("key")), "%${term}%")
            return builder.or(valuesPredicates, keysPredicates)
        } as Predicate[]
        query.where(where)
        return query.restriction
    }

    List<String> buildSearchTerms() {
        return query?.split()?.collect { it.trim().toLowerCase().replace("*", "%") }
    }
}
