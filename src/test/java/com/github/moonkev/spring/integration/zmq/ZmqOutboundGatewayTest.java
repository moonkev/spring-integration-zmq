package com.github.moonkev.spring.integration.zmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;


@ContextConfiguration(locations="/zmq-outbound-gateway-test.xml")
public class ZmqOutboundGatewayTest extends AbstractTestNGSpringContextTests {

	@Autowired
	Gateway gateway;
	
	@Test(groups = {"integration"})
	public void testOutboundGateway() throws Exception {
		for (int i = 0; i < 100; ++i) {
			try {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("message", i);
				map.put("mode", "request");
				gateway.send(map);
			} catch (Exception e) { 
				System.out.println(e.getMessage());
				Thread.sleep(1000); 
			}
		}
		Thread.sleep(1000);
	}
}
