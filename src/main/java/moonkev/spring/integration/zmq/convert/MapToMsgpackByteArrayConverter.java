package moonkev.spring.integration.zmq.convert;

import java.io.IOException;
import java.util.Map;

import org.msgpack.MessagePack;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class MapToMsgpackByteArrayConverter implements Converter<Map<Object, Object>, byte[]> {

	private static final MessagePack msgpack = new MessagePack();
	
	public byte[] convert(Map<Object, Object> map) {

		try {
			return msgpack.write(map);
		} catch (IOException e) {
			throw new ConversionFailedException(
					TypeDescriptor.valueOf(Map.class), 
					TypeDescriptor.valueOf(byte[].class), 
					map, e);
		}
	}
}
