package org.devnull.zuul.web.error

import org.devnull.zuul.service.error.ConflictingOperationException
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.junit.Before
import org.junit.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

class DefaultErrorResolverTest {

    DefaultErrorResolver resolver
    MockHttpServletRequest request
    MockHttpServletResponse response

    @Before
    void createResolver() {
        resolver = new DefaultErrorResolver()
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
        request.userPrincipal = new UsernamePasswordAuthenticationToken("testUser", "fakepassword")
    }

    @Test
    void shouldHaveModelWithCorrectUser() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.model.errorMessage.user == request.userPrincipal.toString()
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldNotBlowUpOnNullUser() {
        request.userPrincipal = null
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert !mv.model.errorMessage.user
    }

    @Test
    void shouldHaveCorrectViewForEmptyResultDataAccessException() {
        def ex = new EmptyResultDataAccessException(1)
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/notFound"
        assert response.status == HttpServletResponse.SC_NOT_FOUND
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldHaveModelWithCorrectRootExceptionInfo() {
        def ex = new RuntimeException("outter", new RuntimeException("middle", new RuntimeException("root")))
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.model.errorMessage.messages == ["root"]
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldHaveCorrectViewForConflictExceptions() {
        def ex = new ConflictingOperationException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/conflict"
        assert mv.model.errorMessage.messages == ["test"]
        assert response.status == HttpServletResponse.SC_CONFLICT
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldHaveCorrectViewForUnhandledExceptions() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/default"
        assert mv.model.errorMessage.messages == ["test"]
        assert response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldHaveCorrectViewForAccesDeniedExceptions() {
        def ex = new AccessDeniedException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/denied"
        assert mv.model.errorMessage.messages == ["test"]
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
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
        assert mv.model.errorMessage.messages == ["Blah does not exist", "Blah must be unique"]
        assert response.status == HttpServletResponse.SC_NOT_ACCEPTABLE
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    @Test
    void shouldEvaluateRootCauseException() {
        def ex = new RuntimeException(new AccessDeniedException("test"))
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/denied"
        assert mv.model.errorMessage.messages == [ex.cause.message]
        assertErrorMessagePropertiesAreNotEmpty(mv.model)
    }

    protected assertErrorMessagePropertiesAreNotEmpty(Map model) {
        assert model.errorMessage instanceof HttpErrorMessage
        def errorMessage = model.errorMessage as HttpErrorMessage
        assert errorMessage.statusCode
        assert errorMessage.date
        assert errorMessage.user
        assert errorMessage.stackTrace
        assert errorMessage.messages
    }
}
