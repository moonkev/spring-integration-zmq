package moonkev.spring.integration.zmq;

import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class ZmqReceivingChannelAdapter extends MessageProducerSupport implements Runnable {
		
	private ZmqContextManager contextManager;
	
	private String address;
	
	private boolean bind = false;
	
	private String topic = null;
	
	private byte[] topicBytes = null;
	
	private Integer socketType;
	
	private Converter<byte[], Object> converter;
	
	private Thread socketThread;
	
	protected final Object startupMonitor = new Object();
			
	public void run() {
		Socket socket = null;
		
		synchronized (startupMonitor) {
			try {
				socket = contextManager.context().createSocket(socketType);
				if (bind) {
					socket.bind(address);
				} else {
					socket.connect(address);
				}
			} finally {
				startupMonitor.notify();
			}
		}
		
		if (socketType == ZMQ.SUB) {
			if (topic == null) {
				socket.subscribe(new byte[]{});
			} else {
				socket.subscribe(topicBytes);
			}
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Object payload = null;
				if (topic == null) {
					byte[] data = socket.recv();
					payload = converter.convert(data);
					sendMessage(MessageBuilder.withPayload(payload).build());
				} else {
					byte[] raw = socket.recv();
					byte[] data = Arrays.copyOfRange(raw, topicBytes.length, raw.length);
					payload = converter.convert(data);					
					sendMessage(MessageBuilder.withPayload(payload).setHeaderIfAbsent("zmq.topic", topic).build());
				}
			} catch (Exception e) {
				if (!contextManager.isRunning()){
					break;
				}
				logger.error("Exception in zmq receiving channel adapter", e);
			}
		}
		socket.close();
	}
		
	protected void onInit() {
		super.onInit();
		Assert.notNull(socketType, "You must provide a socket type");
		Assert.notNull(address, "You must provide a valid ZMQ address");
		Assert.notNull(converter, "You must provide a converter");
	}
	
	protected void doStart() {
		socketThread = new Thread(this);
		socketThread.start();
	}
	
	public void setContextManager(ZmqContextManager contextManager) {
		this.contextManager = contextManager;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setBind(boolean bind) {
		this.bind = bind;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
		this.topicBytes = topic.getBytes(ZMQ.CHARSET);
	}
	
	public void setSocketType(String socketTypeName) {
		socketType = ZmqEndpointUtil.setSocketType(socketTypeName);
	}
	
	public void setConverter(Converter<byte[], Object> converter) {
		this.converter = converter;
	}
}
