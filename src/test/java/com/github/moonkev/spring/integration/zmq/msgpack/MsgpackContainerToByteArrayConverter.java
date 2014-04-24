package com.github.moonkev.spring.integration.zmq.msgpack;

import java.io.IOException;

import org.msgpack.MessagePack;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class MsgpackContainerToByteArrayConverter implements Converter<MsgpackContainer, byte[]> {

	private static final MessagePack msgpack = new MessagePack();
	
	public byte[] convert(MsgpackContainer container) {
		try {
			return msgpack.write(container);
		} catch (IOException e) {
			throw new ConversionFailedException(
					TypeDescriptor.valueOf(MsgpackContainer.class), 
					TypeDescriptor.valueOf(byte[].class), 
					container, e);
		}
	}
}
