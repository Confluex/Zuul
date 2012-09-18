package org.devnull.zuul.web.error

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.error.ConflictingOperationException
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.junit.Before
import org.junit.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

import static org.mockito.Mockito.*

class DefaultErrorResolverTest {

    DefaultErrorResolver resolver
    MockHttpServletRequest request
    MockHttpServletResponse response
    User user

    @Before
    void createResolver() {
        resolver = new DefaultErrorResolver(securityService: mock(SecurityService))
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
        user = new User(email: "test@devnull.org")
        when(resolver.securityService.currentUser).thenReturn(user)
    }

    @Test
    void shouldHaveModelWithCorrectUser() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.model.user == user
    }

    @Test
    void shouldHaveCorrectViewForEmptyResultDataAccessException() {
        def ex = new EmptyResultDataAccessException(1)
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/notFound"
        assert response.status == HttpServletResponse.SC_NOT_FOUND
    }

    @Test
    void shouldHaveModelWithCorrectRootExceptionInfo() {
        def ex = new RuntimeException("outter", new RuntimeException("middle", new RuntimeException("root")))
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.model.error.message == "root"
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    @Test
    void shouldHaveCorrectViewForConflictExceptions() {
        def ex = new ConflictingOperationException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/conflict"
        assert mv.model.error == ex
        assert response.status == HttpServletResponse.SC_CONFLICT
    }

    @Test
    void shouldHaveCorrectViewForUnhandledExceptions() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/default"
        assert mv.model.error == ex
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    @Test
    void shouldHaveCorrectViewForAccesDeniedExceptions() {
        def ex = new AccessDeniedException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/denied"
        assert mv.model.error == ex
        assert response.status == HttpServletResponse.SC_FORBIDDEN
    }

    @Test
    void shouldHaveCorrectViewForConstraintViolations() {
        def violations = [
                new ConstraintViolationImpl(null, "Blah does not exist", null, null, null, null, null, null, null),
                new ConstraintViolationImpl(null, "Blah must be unique", null, null, null, null, null, null, null)
        ] as Set
        def ex = new ConstraintViolationException("Testing validation errors", violations)
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/invalid"
        assert mv.model.error == ex
        assert mv.model.violations == ["Blah does not exist", "Blah must be unique"]
        assert response.status == HttpServletResponse.SC_NOT_ACCEPTABLE
    }

    @Test
    void shouldEvaluateRootCauseException() {
        def ex = new RuntimeException(new AccessDeniedException("test"))
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/denied"
        assert mv.model.error == ex.cause
    }
}
