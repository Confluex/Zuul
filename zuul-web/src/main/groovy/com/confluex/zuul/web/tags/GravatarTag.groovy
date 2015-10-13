package com.confluex.zuul.web.tags

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import com.confluex.security.model.User
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.jsp.tagext.SimpleTagSupport

@Slf4j
class GravatarTag extends SimpleTagSupport {

    Integer size = 32
    User user
    String cssClass = ""

    @Override
    void doTag() {
        def hash = findHash()
        if (hash) {
            def html = new MarkupBuilder(jspContext.out)
            html.escapeAttributes = false
            html.img(src: "http://www.gravatar.com/avatar/${hash}?s=${size}&d=mm", class:cssClass)
        }
    }

    protected String findHash() {
        if (this.user)  return user.emailHash
        def principal = SecurityContextHolder.context?.authentication?.principal
        return principal?.hasProperty("emailHash") ? principal.emailHash : ""
    }
}
