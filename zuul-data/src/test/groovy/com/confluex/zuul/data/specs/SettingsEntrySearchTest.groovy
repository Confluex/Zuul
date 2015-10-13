package com.confluex.zuul.data.specs

import org.junit.Before
import org.junit.Test

class SettingsEntrySearchTest {
    SettingsEntrySearch search

    @Before
    void createSearch() {
        search = new SettingsEntrySearch("a few   SEArch\t terms with a wildca*d")
    }

    @Test
    void shouldRemoveWhitespaceFromSearchTermsAndConvertToLowerCase() {
        assert search.buildSearchTerms() == ["a", "few", "search", "terms", "with", "a", "wildca%d"]
    }


}
