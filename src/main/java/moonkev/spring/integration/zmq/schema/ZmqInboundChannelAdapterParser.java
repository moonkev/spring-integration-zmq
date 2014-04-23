package moonkev.spring.integration.zmq.schema;

import moonkev.spring.integration.zmq.convert.JsonByteArrayToMapConverter;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

public class ZmqInboundChannelAdapterParser extends AbstractChannelAdapterParser {

	protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
		
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
				"moonkev.spring.integration.zmq.ZmqReceivingChannelAdapter");
		
		builder.addPropertyReference("outputChannel", channelName);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "error-channel", "errorChannel");
		builder.addPropertyValue("address", element.getAttribute("address"));
		builder.addPropertyValue("socketType", element.getAttribute("socket-type"));
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "bind");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "topic");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "context-manager");
		if ("".equals(element.getAttribute("converter"))) {
			builder.addPropertyValue("converter", new JsonByteArrayToMapConverter());
		} else {
			builder.addPropertyReference("converter", element.getAttribute("converter"));
		}		
		
		return builder.getBeanDefinition();
	}
}
