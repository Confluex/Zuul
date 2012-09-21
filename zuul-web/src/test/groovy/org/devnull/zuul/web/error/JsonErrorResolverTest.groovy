package org.devnull.zuul.web.error

import groovy.json.JsonSlurper
import org.devnull.security.model.User
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException
import org.devnull.zuul.data.model.SettingsEntry
import org.springframework.validation.BeanPropertyBindingResult
import org.devnull.zuul.service.error.ValidationException

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
    void shouldRenderValidationExceptions() {
        def entry = new SettingsEntry()
        def errors = new BeanPropertyBindingResult(entry, "testEntry")
        errors.reject(null, "Entry is invalid")
        errors.rejectValue("key", null, "key must be unique")
        errors.rejectValue("key", null, "key must not contain special characters")
        def ex = new ValidationException(errors)
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.isEmpty()
        assert response.status == HttpServletResponse.SC_NOT_ACCEPTABLE
        def json = new JsonSlurper().parseText(response.contentAsString)
        assert json.fieldMessages["key"][0] == "key must be unique"
        assert json.fieldMessages["key"][1] == "key must not contain special characters"
        assert json.messages == ["Entry is invalid"]
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
        assert json.statusCode
    }
}
