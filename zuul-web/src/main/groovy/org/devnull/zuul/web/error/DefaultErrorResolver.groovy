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
import org.springframework.dao.EmptyResultDataAccessException

class DefaultErrorResolver implements HandlerExceptionResolver {

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
        switch (root) {
            case ConstraintViolationException:
                def cve = root as ConstraintViolationException
                log.warn("Constraint violation: {}", root.message)
                view = "/error/invalid"
                model.violations = cve.constraintViolations.collect { it.message }
                response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
                break;
            case ConflictingOperationException:
                log.warn("Conflicing operation: {}", root.message)
                view = "/error/conflict"
                response.status = HttpServletResponse.SC_CONFLICT
                break;
            case AccessDeniedException:
                // the /403.jsp in web.xml is still needed for errors which occur outside of the spring security
                // I'm unsure how this is still happening but it does from time to time. I neeed to figur out why.
                log.info("User {} was denied access to {}", model.user, model.requestUri)
                view = "/error/denied"
                response.status = HttpServletResponse.SC_FORBIDDEN
                break;
            case EmptyResultDataAccessException:
                log.info("Unable to find data", root.message)
                view = "/error/notFound"
                response.status = HttpServletResponse.SC_NOT_FOUND
                break;
            default:
                log.error("Unhandled exception", root)
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }

        return new ModelAndView(view, model)
    }

}
