package com.confluex.zuul.web.config

import org.springframework.validation.Errors
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.ServletContext

class JstlFunctions {
    static String getApplicationVersion(ServletContext context) {
        def mavenProps = new ServletContextResource(context, "/META-INF/maven/com.confluex/zuul-web/pom.properties")
        if (mavenProps.exists()) {
            def properties = new Properties()
            def stream = mavenProps.inputStream
            properties.load(stream)
            stream.close()
            return properties.getProperty("version") ?: "unknown"
        } else {
            return "development"
        }
    }

    static Map<String, List<String>> groupErrorsByField(Errors errors) {
        def byField = [:]
        errors.fieldErrors?.groupBy { it.field }?.each { fieldName, fieldErrors ->
            byField[fieldName] = fieldErrors.collect { it.defaultMessage }
        }
        return byField
    }

    static String join(List list, String token = "<br/>") {
        return list.join(token)
    }

    static <T> T find(Collection<T> collection, String property, Object value) {
        return collection.find {
           it.properties[property] == value
        }
    }


}
