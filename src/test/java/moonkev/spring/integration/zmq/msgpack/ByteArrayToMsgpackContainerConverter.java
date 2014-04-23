package moonkev.spring.integration.zmq.msgpack;

import java.io.IOException;

import org.msgpack.MessagePack;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class ByteArrayToMsgpackContainerConverter implements Converter<byte[], MsgpackContainer> {

	private static final MessagePack msgpack = new MessagePack();
	
	public MsgpackContainer convert(byte[] bytes) {
		try {
			return msgpack.read(bytes, MsgpackContainer.class);
		} catch (IOException e) {
			throw new ConversionFailedException(
					TypeDescriptor.valueOf(byte[].class), 
					TypeDescriptor.valueOf(MsgpackContainer.class), 
					bytes, e);
		}
	}
}
