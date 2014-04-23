package moonkev.spring.integration.zmq.schema;

import moonkev.spring.integration.zmq.convert.JsonByteArrayToMapConverter;
import moonkev.spring.integration.zmq.convert.MapToJsonByteArrayConverter;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

public class ZmqOutboundGatewayParser extends AbstractConsumerEndpointParser {

	protected String getInputChannelAttributeName() {
		return "request-channel";
	}
	
	protected BeanDefinitionBuilder parseHandler(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
				"moonkev.spring.integration.zmq.ZmqOutboundGateway");
		
		builder.addPropertyValue("address", element.getAttribute("address"));
		builder.addPropertyValue("socketType", element.getAttribute("socket-type"));
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "bind");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "send-timeout");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "receive-timeout");
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
