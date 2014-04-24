package com.github.moonkev.spring.integration.zmq.convert;

import java.io.IOException;
import java.util.Map;

import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class MsgpackByteArrayToMapConverter implements Converter<byte[], Map<Value, Value>> {

	private static final MessagePack msgpack = new MessagePack();
	
	private static final Template<Map<Value, Value>> template = Templates.tMap(Templates.TValue, Templates.TValue);
	
	public Map<Value, Value> convert(byte[] bytes) {
		
		try {
			return msgpack.read(bytes, template);
		} catch (IOException e) {
			throw new ConversionFailedException(
					TypeDescriptor.valueOf(Map.class), 
					TypeDescriptor.valueOf(byte[].class), 
					bytes, e);
		}
	}
}
