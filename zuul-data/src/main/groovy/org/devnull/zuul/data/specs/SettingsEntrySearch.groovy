package org.devnull.zuul.data.specs

import org.devnull.zuul.data.model.SettingsEntry
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
        def group = root.join("group", JoinType.INNER)
        def env = group.join("environment", JoinType.INNER)
        def where = terms.collect { term ->
            def valuesPredicates = builder.like(builder.lower(root.get("value")), "%${term}%")
            def keysPredicates = builder.like(builder.lower(root.get("key")), "%${term}%")
            def groupPredicates = builder.like(builder.lower(group.get("name")), "%${term}%")
            def envPredicates = builder.like(builder.lower(env.get("name")), "%${term}%")
            return builder.or(valuesPredicates, keysPredicates, groupPredicates, envPredicates)
        } as Predicate[]
        query.where(where)
        return query.restriction
    }

    List<String> buildSearchTerms() {
        return query?.split()?.collect { it.trim().toLowerCase().replace("*", "%") }
    }
}
