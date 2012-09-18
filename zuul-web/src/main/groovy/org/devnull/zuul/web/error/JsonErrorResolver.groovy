package org.devnull.zuul.web.error

import groovy.json.JsonBuilder
import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.security.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

class JsonErrorResolver implements HandlerExceptionResolver {

    final def log = LoggerFactory.getLogger(this.class)

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def root = ExceptionUtils.getRootCause(ex) ?: ex
        switch (root) {
            case ConstraintViolationException:
                def cve = root as ConstraintViolationException
                log.warn("Constraint violation: {}", root.message)
                def errorMsg = new HttpErrorMessage(
                        requestUri: request.requestURI,
                        user: request.userPrincipal.toString(),
                        stackTrace: ExceptionUtils.getStackTrace(root),
                        messages: cve.constraintViolations.collect { it.message }
                )
                response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
                renderJsonObject(errorMsg, response)
                break;
            default:
                log.error("Unhandled exception", root)
                def errorMsg = new HttpErrorMessage(
                        requestUri: request.requestURI,
                        user: request.userPrincipal.toString(),
                        stackTrace: ExceptionUtils.getStackTrace(root),
                        messages: [ root.message ]
                )
                response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                renderJsonObject(errorMsg, response)
        }

        return new ModelAndView()
    }

    protected void renderJsonObject(Object content, HttpServletResponse response) {
        def writer = new OutputStreamWriter(response.outputStream)
        new JsonBuilder(content).writeTo(writer)
        writer.close()
    }

}
