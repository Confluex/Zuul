package org.devnull.zuul.service.error

import org.springframework.validation.Errors

class ValidationException extends ZuulServiceException {
    Errors errors

    ValidationException(Errors errors, Exception e = null) {
        super("Invalid data", e)
        this.errors = errors
    }

    Map<String, List<String>> getFieldErrors() {
        def byField = errors.fieldErrors.groupBy { it.field }
        byField.each { k, v ->
            byField[k] = v.flatten().collect { it.defaultMessage }
        }
        return byField as Map<String, List<String>>
    }

    List<String> getGlobalErrors() {
        return errors.globalErrors.collect { it.defaultMessage }
    }

    String toString() {
        return "ValidationException{globalErrors: ${globalErrors}, fieldErrors: ${fieldErrors}"
    }
}
