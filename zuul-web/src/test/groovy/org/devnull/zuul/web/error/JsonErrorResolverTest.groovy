package org.devnull.zuul.web.error

import org.devnull.security.model.User
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

import static org.mockito.Mockito.*
import org.devnull.security.service.SecurityService

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
        resolver.securityService = mock(SecurityService)
        when(resolver.securityService.currentUser).thenReturn(user)
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
        assert response.contentAsString == '["Blah must be unique","Blah does not exist"]'
    }

    @Test
    void shouldRenderUnhandledExceptions() {
        def mv = resolver.resolveException(request, response, null, new RuntimeException("test"))
        assert mv.isEmpty()
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assert response.contentAsString == '["test"]'
    }
}
