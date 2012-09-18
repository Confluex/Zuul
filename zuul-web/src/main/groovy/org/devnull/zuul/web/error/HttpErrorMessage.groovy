package org.devnull.zuul.web.error

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString(includeNames=true)
@EqualsAndHashCode
class HttpErrorMessage {
    String stackTrace
    List<String> messages = []
    String requestUri
    Date date = new Date()
    String user
}
