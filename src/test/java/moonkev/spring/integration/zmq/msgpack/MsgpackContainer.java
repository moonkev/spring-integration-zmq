package moonkev.spring.integration.zmq.msgpack;

import java.util.List;
import java.util.Map;

import org.msgpack.annotation.Message;

@Message
public class MsgpackContainer {

	public Integer intField;
	public Double doubleField;
	public String stringField;
	public List<String> listField;
	public Map<String, String> mapField;
	
	public String toString() {
		return String.format("MsgpackContainer:[%d, %f, %s, %s, %s]",
				intField, doubleField, stringField, listField, mapField);
	}
}
