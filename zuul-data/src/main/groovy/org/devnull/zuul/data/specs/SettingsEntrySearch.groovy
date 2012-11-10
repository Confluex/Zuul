package org.devnull.zuul.data.specs

import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.CriteriaBuilder
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString(includeNames=true)
@EqualsAndHashCode
class SettingsEntrySearch  implements Specification<SettingsEntry> {

    String query

    SettingsEntrySearch(String query) {
        this.query = query
    }

    Predicate toPredicate(Root<SettingsEntry> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        def terms = buildSearchTerms()
        terms.each { term ->
            query.where(builder.like(builder.lower(root.get("key")),"%${term}%"))
        }
        return query.restriction
    }

    List<String> buildSearchTerms() {
        return query?.split()?.collect { it.trim().toLowerCase() }
    }
}
