package com.github.moonkev.spring.integration.zmq.schema;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class NamespaceHandler extends AbstractIntegrationNamespaceHandler {

	public void init() {
		this.registerBeanDefinitionParser("inbound-channel-adapter", new ZmqInboundChannelAdapterParser());
		this.registerBeanDefinitionParser("outbound-channel-adapter", new ZmqOutboundChannelAdapterParser());
		this.registerBeanDefinitionParser("outbound-gateway", new ZmqOutboundGatewayParser());
		this.registerBeanDefinitionParser("lazy-pirate-gateway", new ZmqLazyPirateGatewayParser());
		this.registerBeanDefinitionParser("context-manager", new ZmqContextManagerParser());
	}
}
