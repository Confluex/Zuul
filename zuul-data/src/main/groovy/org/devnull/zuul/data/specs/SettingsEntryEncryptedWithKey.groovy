package org.devnull.zuul.data.specs

import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.data.jpa.domain.Specification

import javax.persistence.criteria.*

class SettingsEntryEncryptedWithKey implements Specification<SettingsEntry> {
    EncryptionKey key

    SettingsEntryEncryptedWithKey(EncryptionKey key) {
        this.key = key
    }

    Predicate toPredicate(Root<SettingsEntry> entry, CriteriaQuery<?> query, CriteriaBuilder builder) {
        def group = entry.join("group", JoinType.INNER)
        query.where(
                builder.equal(group.get("key"), key),
                builder.equal(entry.get("encrypted"), true)
        )
        return query.restriction
    }
}
