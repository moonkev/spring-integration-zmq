package com.github.moonkev.spring.integration.zmq;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.Lifecycle;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.util.Assert;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class ZmqLazyPirateGateway extends AbstractReplyProducingMessageHandler
		implements Lifecycle, ZmqContextShutdownListener {

	protected final ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private ZmqContextManager contextManager;
	
	private Converter<Object, byte[]> requestConverter;

	private Converter<byte[], Object> replyConverter;

	private String address;

	private volatile boolean running = false;

	protected final Object lifecycleMonitor = new Object();
	
	private int retryCount = 0;
	
	private int socketSendTimeout = -1;
	
	private int socketReceiveTimeout = -1;
	
	private int linger = 0;
	
	private Socket socket;
	
	public boolean isRunning() {
		return running;
	}

	public void shutdownZmq() {
		stop();
	}
	
	protected void doInit() {
		super.doInit();
		Assert.notNull(address, "You must provide a valid ZMQ address");
		Assert.notNull(requestConverter, "You must provide a requestConverter");
		Assert.notNull(replyConverter, "You must provide a replyConverter");
	}
	
	public void connect() {
		if (socket != null) {
			contextManager.context().destroySocket(socket);
		}
		socket = contextManager.context().createSocket(ZMQ.REQ);
		socket.setSendTimeOut(socketSendTimeout);
		socket.setLinger(linger);
		socket.connect(address);
	}

	public void start() {
		synchronized (lifecycleMonitor) {
			if (!running) {
				Future<Void> response = executorService.submit(new Callable<Void>() {
					public Void call() throws Exception {
						ZmqLazyPirateGateway.this.connect();
						return null;
					}
				});
				try {
					response.get();
				} catch (Throwable t) {
					throw new BeanCreationException("Error starting zmq outbound gateway", t);
				}
				contextManager.registerShutdownListener(this);
				running = true;
			}
		}
	}

	public void stop() {
		synchronized (lifecycleMonitor) {
			if (running) {
				running = false;
				Future<Void> response = executorService.submit(new Callable<Void>() {
					public Void call() throws Exception {
						contextManager.context().destroySocket(socket);
						return null;
					}
				});
				try {
					response.get();
					executorService.shutdown();
				} catch (Throwable t) {
					logger.error("Error shutting down socket in zmq outbound gateway");
				}
			}
		}
	}

	protected Object handleRequestMessage(final Message<?> requestMessage) {
		if (!running) {
			return null;
		}
		
		Future<Object> response = executorService.submit(new Callable<Object>() {
			public Object call() throws Exception {
				byte[] requestData = requestConverter.convert(requestMessage.getPayload());
				int retriesLeft = retryCount;
				while (!Thread.currentThread().isInterrupted()) {
					socket.send(requestData);
					PollItem items[] = { new PollItem(socket, Poller.POLLIN) };
					int rc = ZMQ.poll(items, socketReceiveTimeout);
					if (rc == -1) {
						break;
					}
					if (items[0].isReadable()) {
						byte[] reply = socket.recv();
						return reply;
					} else if (--retriesLeft == 0) {
						break;
					} else {
						ZmqLazyPirateGateway.this.connect();
					}
				}
				ZmqLazyPirateGateway.this.connect();
				return null;
			}
		});
				
		try {
			return response.get();
		} catch (Throwable t) {
			throw new MessageHandlingException(requestMessage, t);
		}
	}
	
	public void setContextManager(ZmqContextManager contextManager) {
		this.contextManager = contextManager;
	}

	public void setReplyConverter(Converter<byte[], Object> replyConverter) {
		this.replyConverter = replyConverter;
	}

	public void setRequestConverter(Converter<Object, byte[]> requestConverter) {
		this.requestConverter = requestConverter;
	}

	public void setReplyChannel(MessageChannel replyChannel) {
		this.setOutputChannel(replyChannel);
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	public void setSocketSendTimeout(int socketSendTimeout) {
		this.socketSendTimeout = socketSendTimeout;
	}
	
	public void setSocketReceiveTimeout(int socketReceiveTimeout) {
		this.socketReceiveTimeout = socketReceiveTimeout;
	}
	
	public void setLinger(int linger) {
		this.linger = linger;
	}
}
