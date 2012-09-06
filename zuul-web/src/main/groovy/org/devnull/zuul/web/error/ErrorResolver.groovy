package org.devnull.zuul.web.error

import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.zuul.service.error.ConflictingOperationException
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.devnull.security.service.SecurityService
import javax.validation.ConstraintViolationException

class ErrorResolver implements HandlerExceptionResolver {

    @Autowired
    SecurityService securityService

    final def log = LoggerFactory.getLogger(this.class)

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String view = "/error/default"
        def root = ExceptionUtils.getRootCause(ex) ?: ex
        def model = [
                error: root,
                stackTrace: ExceptionUtils.getStackTrace(root),
                requestUri: request.requestURI,
                date: new Date(),
                user: securityService.currentUser
        ]
        switch (ex) {
            case ConstraintViolationException:
                def cve = ex as ConstraintViolationException
                log.warn("Constraint violation: {}", ex.message)
                view = "/error/invalid"
                model.violations = cve.constraintViolations.collect { it.message }
                break;
            case ConflictingOperationException:
                log.warn("Conflicing operation: {}", ex.message)
                view = "/error/conflict"
                break;
            case AccessDeniedException:
                // the /403.jsp in web.xml is still needed for errors which occur outside of the spring security
                // I'm unsure how this is still happening but it does from time to time. I neeed to figur out why.
                log.info("User {} was denied access to {}", model.user, model.requestUri)
                view = "/error/denied"
                break;
            default:
                log.error("Unhandled exception", ex)
        }

        return new ModelAndView(view, model)
    }

}
