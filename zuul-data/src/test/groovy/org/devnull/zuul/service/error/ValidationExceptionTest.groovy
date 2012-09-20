package org.devnull.zuul.service.error

import org.devnull.security.model.User
import org.junit.Test
import org.springframework.validation.BeanPropertyBindingResult

class ValidationExceptionTest {
    @Test
    void shouldGroupErrorsByField() {
        def user = new User()
        def errors = new BeanPropertyBindingResult(user, "user")
        errors.rejectValue("firstName", null, "Must have at least 2 characters")
        errors.rejectValue("password", null, "Must have upper and lower case characters")
        errors.rejectValue("password", null, "Must have have at least 8 characters")
        errors.rejectValue("password", null, "Cannot contain your user name")
        def errorsByField = new ValidationException(errors).fieldErrors
        assert errorsByField.size() == 2
        assert errorsByField["firstName"].size() == 1
        assert errorsByField["firstName"].first() == "Must have at least 2 characters"
        assert errorsByField["password"].size() == 3
        assert errorsByField["password"][0] == "Must have upper and lower case characters"
        assert errorsByField["password"][1] == "Must have have at least 8 characters"
        assert errorsByField["password"][2] == "Cannot contain your user name"
    }

    @Test
    void shouldFindGlobalErrors() {
        def user = new User()
        def errors = new BeanPropertyBindingResult(user, "user")
        errors.reject(null, "Duplicate user")
        def globalErrors = new ValidationException(errors).globalErrors
        assert globalErrors.size() == 1
        assert globalErrors.first() == "Duplicate user"
    }
}
