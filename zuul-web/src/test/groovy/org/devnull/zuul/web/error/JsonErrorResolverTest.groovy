package org.devnull.zuul.web.error

import groovy.json.JsonSlurper
import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

import static org.mockito.Mockito.*
import java.security.Principal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class JsonErrorResolverTest {
    JsonErrorResolver resolver
    MockHttpServletRequest request
    MockHttpServletResponse response
    User user

    @Before
    void createResolver() {
        resolver = new JsonErrorResolver()
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
        user = new User(email: "test@devnull.org")

        request.userPrincipal = new UsernamePasswordAuthenticationToken("testUser", "fakepassword")
    }

    @Test
    void shouldRenderConstraintsValidationExceptions() {
        def violations = [
                new ConstraintViolationImpl(null, "Blah does not exist", null, null, null, null, null, null, null),
                new ConstraintViolationImpl(null, "Blah must be unique", null, null, null, null, null, null, null)
        ] as Set
        def ex = new ConstraintViolationException("Testing validation errors", violations)
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.isEmpty()
        assert response.status == HttpServletResponse.SC_NOT_ACCEPTABLE
        def json = new JsonSlurper().parseText(response.contentAsString)
        assert json.messages == ["Blah does not exist", "Blah must be unique"]
        assertCommonValuesAreNotEmpty(json)
    }

    @Test
    void shouldRenderUnhandledExceptions() {
        def mv = resolver.resolveException(request, response, null, new RuntimeException("test"))
        assert mv.isEmpty()
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        def json = new JsonSlurper().parseText(response.contentAsString)
        assert json.messages == ["test"]
        assertCommonValuesAreNotEmpty(json)
    }

    protected void assertCommonValuesAreNotEmpty(json) {
        assert json.user
        assert json.date
        assert json.stackTrace
    }
}
