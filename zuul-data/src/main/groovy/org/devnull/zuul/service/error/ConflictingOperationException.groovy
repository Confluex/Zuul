package org.devnull.zuul.service.error

class ConflictingOperationException extends ZuulServiceException {
    ConflictingOperationException(String message, Exception e = null) {
        super(message, e)
    }
}
