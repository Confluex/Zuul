package com.confluex.zuul.web.config

import groovy.mock.interceptor.MockFor
import com.confluex.security.model.User
import com.confluex.zuul.service.security.KeyConfiguration
import org.junit.Test
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.ServletContext

import static org.mockito.Mockito.*

class JstlFunctionsTest {
    @Test
    void shouldFindMavenProjectVersion() {
        def resource = new MockFor(ServletContextResource)
        resource.demand.exists { return true }
        resource.demand.getInputStream { return new ByteArrayInputStream("version=1.2.3".bytes)}
        resource.use {
            assert JstlFunctions.getApplicationVersion(mock(ServletContext)) == "1.2.3"
        }
    }

    @Test
    void shouldFindDevelopmentProjectVersionWhenNotBuiltByMaven() {
        def resource = new MockFor(ServletContextResource)
        resource.demand.exists { return false }
        resource.use {
            assert JstlFunctions.getApplicationVersion(mock(ServletContext)) == "development"
        }
    }

    @Test
    void shouldGroupSpringErrorsByField() {
        def user = new User()
        def errors = new BeanPropertyBindingResult(user, "user")
        errors.rejectValue("firstName", null, "Must have at least 2 characters")
        errors.rejectValue("password", null, "Must have upper and lower case characters")
        errors.rejectValue("password", null, "Must have have at least 8 characters")
        errors.rejectValue("password", null, "Cannot contain your user name")
        def errorsByField = JstlFunctions.groupErrorsByField(errors)
        assert errorsByField.size() == 2
        assert errorsByField["firstName"].size() == 1
        assert errorsByField["firstName"].first() == "Must have at least 2 characters"
        assert errorsByField["password"].size() == 3
        assert errorsByField["password"][0] == "Must have upper and lower case characters"
        assert errorsByField["password"][1] == "Must have have at least 8 characters"
        assert errorsByField["password"][2] == "Cannot contain your user name"
    }

    @Test
    void shouldJoinListsWithDefaultToken() {
        def list = ["abc", "def"]
        def join = JstlFunctions.join(list)
        assert join == "abc<br/>def"
    }

    @Test
    void shouldJoinListsWithProvidedToken() {
        def list = ["abc", "def"]
        def join = JstlFunctions.join(list, " - ")
        assert join == "abc - def"
    }

    @Test
    void shouldFindEntryInCollectionByPropertyNameAndValue() {
        def configs = [
                new KeyConfiguration(algorithm: "BPE-ABC"),
                new KeyConfiguration(algorithm: "BPE-DEF"),
                new KeyConfiguration(algorithm: "BPE-HIJ")
        ]
        assert JstlFunctions.find(configs, 'algorithm', 'BPE-DEF') == configs[1]
        assert JstlFunctions.find(configs, 'algorithm', 'NOTVALID') == null
        assert JstlFunctions.find(configs, 'algorithm', null) == null
        assert JstlFunctions.find(configs, 'badproperty', 'BPE-DEF') == null
    }


}
