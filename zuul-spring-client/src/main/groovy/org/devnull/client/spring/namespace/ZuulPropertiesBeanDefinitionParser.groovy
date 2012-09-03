package org.devnull.client.spring.namespace

import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
import org.w3c.dom.Element
import org.devnull.client.spring.ZuulPropertiesFactoryBean
import org.springframework.util.xml.DomUtils
import org.devnull.client.spring.cache.PropertiesObjectFileSystemStore

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
        def fileStore = DomUtils.getChildElementByTagName(element, "file-store")
        if (fileStore) {
            def fileStoreFactory = BeanDefinitionBuilder.rootBeanDefinition(PropertiesObjectFileSystemStore);
            def path = fileStore.getAttribute("path")
            if (path) {
                fileStoreFactory.addConstructorArg(path)
            }
            bean.addPropertyValue("propertiesStore", fileStoreFactory.beanDefinition)
        }
    }
}
