package org.devnull.zuul.web.config

import org.springframework.validation.Errors
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.ServletContext

class JstlFunctions {
    static String getApplicationVersion(ServletContext context) {
        def mavenProps = new ServletContextResource(context, "/META-INF/maven/org.devnull/zuul-web/pom.properties")
        if (mavenProps.exists()) {
            def properties = new Properties()
            def stream = mavenProps.inputStream
            properties.load(stream)
            stream.close()
            return properties.getProperty("version") ?: "unknown"
        }
        else {
            return "development"
        }
    }

    static Map<String, List<String>> groupErrorsByField(Errors errors) {
        def byField = errors.fieldErrors.groupBy { it.field }
        byField.each { k, v ->
            byField[k] = v.flatten().collect { it.defaultMessage }
        }
        return byField
    }


}
