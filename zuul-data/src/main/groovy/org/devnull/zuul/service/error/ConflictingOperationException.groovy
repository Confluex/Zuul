package org.devnull.zuul.service.error

class ConflictingOperationException extends ZuulServiceException {
    Integer code = 406

    ConflictingOperationException(String message, Exception e = null) {
        super(message, e)
    }
}
