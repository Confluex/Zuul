package org.devnull.zuul.web.error

import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.security.service.SecurityService
import org.devnull.zuul.service.error.ConflictingOperationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException
import groovy.json.JsonBuilder

class JsonErrorResolver implements HandlerExceptionResolver {

    @Autowired
    SecurityService securityService

    final def log = LoggerFactory.getLogger(this.class)

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def root = ExceptionUtils.getRootCause(ex) ?: ex
        //TODO create a rest error object for consistent rendering
        switch (root) {
            case ConstraintViolationException:
                def cve = root as ConstraintViolationException
                log.warn("Constraint violation: {}", root.message)
                def violations = cve.constraintViolations.collect { it.message }
                response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
                renderJsonObject(violations, response)
                break;
            default:
                log.error("Unhandled exception", root)
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                renderJsonObject([root.message], response)
        }

        return new ModelAndView()
    }

    protected void renderJsonObject(Object content, HttpServletResponse response) {
        def writer = new OutputStreamWriter(response.outputStream)
        new JsonBuilder(content).writeTo(writer)
        writer.close()
    }

}
