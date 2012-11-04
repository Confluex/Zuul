package org.devnull.zuul.web.tags

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import org.devnull.security.model.User
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.jsp.tagext.SimpleTagSupport

@Slf4j
class GravatarTag extends SimpleTagSupport {

    Integer size = 32

    @Override
    void doTag() {
        def authentication = SecurityContextHolder.context.authentication
        if (authentication?.principal instanceof User) {
            def hash = authentication.principal.emailHash
            def html = new MarkupBuilder(jspContext.out)
            html.escapeAttributes = false
            html.img(src: "http://www.gravatar.com/avatar/${hash}?s=${size}&d=mm")
        }
        else {
            log.info("Unsupported principal: {} with type: {}", authentication?.principal, authentication?.principal?.class)
        }
    }
}
