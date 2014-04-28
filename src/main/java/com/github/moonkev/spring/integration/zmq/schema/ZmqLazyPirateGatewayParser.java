package com.github.moonkev.spring.integration.zmq.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

import com.github.moonkev.spring.integration.zmq.convert.JsonByteArrayToMapConverter;
import com.github.moonkev.spring.integration.zmq.convert.MapToJsonByteArrayConverter;

public class ZmqLazyPirateGatewayParser extends AbstractConsumerEndpointParser {
 
	protected String getInputChannelAttributeName() {
		return "request-channel";
	}
	
	protected BeanDefinitionBuilder parseHandler(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
				"com.github.moonkev.spring.integration.zmq.ZmqLazyPirateGateway");
		
		builder.addPropertyValue("address", element.getAttribute("address"));
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "retry-count");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "send-timeout", "socketSendTimeout");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "receive-timeout", "socketReceiveTimeout");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "linger");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "reply-channel");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "context-manager");
		
		if ("".equals(element.getAttribute("request-converter"))) {
			builder.addPropertyValue("requestConverter", new MapToJsonByteArrayConverter());
		} else {
			builder.addPropertyReference("request-converter", element.getAttribute("requestConverter"));
		}
		
		if ("".equals(element.getAttribute("reply-converter"))) {
			builder.addPropertyValue("replyConverter", new JsonByteArrayToMapConverter());
		} else {
			builder.addPropertyReference("reply-converter", element.getAttribute("replyConverter"));
		}

		return builder;	
	}
}
