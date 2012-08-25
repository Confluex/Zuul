package org.devnull.zuul.web.error

import org.devnull.security.model.User
import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.error.ConflictingOperationException
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException

import static org.mockito.Mockito.*

class ErrorResolverTest {

    ErrorResolver resolver
    MockHttpServletRequest request
    MockHttpServletResponse response
    User user

    @Before
    void createResolver() {
        resolver = new ErrorResolver(securityService: mock(SecurityService))
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
    void shouldHaveModelWithCorrectRootExceptionInfo() {
        def ex = new RuntimeException("outter", new RuntimeException("middle", new RuntimeException("root")))
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.model.error.message == "root"
    }

    @Test
    void shouldHaveCorrectViewForConflictExceptions() {
        def ex = new ConflictingOperationException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/conflict"
        assert mv.model.error == ex
    }

    @Test
    void shouldHaveCorrectViewForUnhandledExceptions() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/default"
        assert mv.model.error == ex
    }

    @Test
    void shouldHaveCorrectViewForAccesDeniedExceptions() {
        def ex = new AccessDeniedException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/denied"
        assert mv.model.error == ex
    }
}
