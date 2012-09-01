package org.devnull.client.spring.namespace

import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
import org.w3c.dom.Element
import org.devnull.client.spring.ZuulPropertiesFactoryBean

class ZuulPropertiesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    protected Class getBeanClass(Element element) {
        return ZuulPropertiesFactoryBean
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        def config = element.getAttribute("config")
        bean.addConstructorArg(config)
        def httpClientRef = element.getAttribute("http-client-ref")
        if (httpClientRef) {
            bean.addPropertyReference("httpClient", httpClientRef)
        }
        ZuulPropertiesFactoryBean.OPTIONAL_ATTRIBUTES.each {
            def option = element.getAttribute(it)
            if (option) {
                bean.addPropertyValue(it, option)
            }
        }
    }
}
