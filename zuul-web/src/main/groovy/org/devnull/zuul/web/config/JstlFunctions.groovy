package org.devnull.zuul.web.config

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
}
