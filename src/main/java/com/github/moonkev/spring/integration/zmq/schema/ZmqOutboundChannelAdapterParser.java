package com.github.moonkev.spring.integration.zmq.schema;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

import com.github.moonkev.spring.integration.zmq.convert.MapToJsonByteArrayConverter;

public class ZmqOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser  {

	protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
				"com.github.moonkev.spring.integration.zmq.ZmqSendingMessageHandler");
		
		builder.addPropertyValue("address", element.getAttribute("address"));
		builder.addPropertyValue("socketType", element.getAttribute("socket-type"));
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "bind");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "topic");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "context-manager");
		if ("".equals(element.getAttribute("converter"))) {
			builder.addPropertyValue("converter", new MapToJsonByteArrayConverter());
		} else {
			builder.addPropertyReference("converter", element.getAttribute("converter"));
		}		
		
		return builder.getBeanDefinition();
	}
}
