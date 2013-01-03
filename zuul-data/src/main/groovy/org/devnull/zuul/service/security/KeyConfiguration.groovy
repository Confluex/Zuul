package org.devnull.zuul.service.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true)
class KeyConfiguration {
    String id
    String algorithm
    String provider
    Integer hashIterations
    String description
}
