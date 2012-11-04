package org.devnull.zuul.web.tags

import org.devnull.security.model.User
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockJspWriter
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.jsp.JspContext

import static org.mockito.Mockito.*
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority

class GravatarTagTest {
    GravatarTag tag

    @Before
    void createTag() {
        tag = new GravatarTag(jspContext: mock(JspContext))
        def user = new User(email: "test@devnull.org")
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(user, "********")
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldRenderLinkForLoggedInUsers() {
        def response = new MockHttpServletResponse()
        def writer = new MockJspWriter(response)
        when(tag.jspContext.getOut()).thenReturn(writer)
        tag.doTag()
        assert response.contentAsString == "<img src='http://www.gravatar.com/avatar/9b345d5ef6f1397f6ae1e28a5794d4c1?s=32&d=mm' />"
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldRenderNothingForNonAuthenticatedUsers() {
        SecurityContextHolder.context.authentication = null
        def response = new MockHttpServletResponse()
        def writer = new MockJspWriter(response)
        when(tag.jspContext.getOut()).thenReturn(writer)
        tag.doTag()
        assert response.contentAsString == ""
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void shouldRenderNothingForAnonymous() {
        SecurityContextHolder.context.authentication = new AnonymousAuthenticationToken("abc", "anonymous coward", [new SimpleGrantedAuthority("ROLE_FAKE")])
        def response = new MockHttpServletResponse()
        def writer = new MockJspWriter(response)
        when(tag.jspContext.getOut()).thenReturn(writer)
        tag.doTag()
        assert response.contentAsString == ""
    }
}
