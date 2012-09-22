package org.devnull.zuul.web.error

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.zuul.service.error.ConflictingOperationException
import org.devnull.zuul.service.error.ValidationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

/**
 * Use by ErrorResolvers as a container to pass error messages to HTTP clients.
 */
@Slf4j
@ToString(includeNames = true)
@EqualsAndHashCode
class HttpErrorMessage {

    String stackTrace
    List<String> messages = []
    Map<String, List<String>> fieldMessages = [:]
    String requestUri
    Date date = new Date()
    String user
    Integer statusCode


    HttpErrorMessage(ConflictingOperationException ex, HttpServletRequest request) {
        log.warn("Conflicing operation: {}", ex.message)
        this.messages = [ex.message]
        this.statusCode = HttpServletResponse.SC_CONFLICT
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
    }

    HttpErrorMessage(AccessDeniedException ex, HttpServletRequest request) {
        log.info("User {} was denied access to {}", request.userPrincipal?.toString(), request.requestURI)
        this.messages = [ex.message]
        this.statusCode = HttpServletResponse.SC_FORBIDDEN
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
    }

    HttpErrorMessage(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.message)
        this.messages = ex.constraintViolations.collect { it.message }
        this.statusCode = HttpServletResponse.SC_NOT_ACCEPTABLE
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
    }

    HttpErrorMessage(EmptyResultDataAccessException ex, HttpServletRequest request) {
        log.warn("Unable to find requested data", ex.message)
        this.messages = [ex.message]
        this.statusCode = HttpServletResponse.SC_NOT_FOUND
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
    }

    HttpErrorMessage(ValidationException ex, HttpServletRequest request) {
        log.warn("Validation errors: {}", ex.toString())
        this.messages = ex.globalErrors
        this.fieldMessages = ex.fieldErrors
        this.statusCode = HttpServletResponse.SC_NOT_ACCEPTABLE
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
    }

    HttpErrorMessage(Throwable ex, HttpServletRequest request) {
        log.warn("Unhandled error", ex)
        this.messages = [ex.message]
        this.stackTrace = ExceptionUtils.getStackTrace(ex)
        this.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        this.user = request.userPrincipal?.toString()
        this.requestUri = request.requestURI
    }


}
