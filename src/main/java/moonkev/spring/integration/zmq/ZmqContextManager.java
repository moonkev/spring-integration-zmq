package moonkev.spring.integration.zmq;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.context.Lifecycle;
import org.zeromq.ZContext;

public class ZmqContextManager implements Lifecycle {

	private ZContext context;
		
	private volatile boolean running = false;
		
	private Collection<ZmqContextShutdownListener> shutdownListeners = new HashSet<ZmqContextShutdownListener>();
	
	public ZmqContextManager(int ioThreads) {
		context = new ZContext(ioThreads);
	}
	
	public ZContext context() {
		return context;
	}

	public void start() {
		running = true;
	}
	
	public void stop() {
		running = false;
		for (ZmqContextShutdownListener listener : shutdownListeners) {
			listener.shutdownZmq();
		}
		context.getContext().term();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void registerShutdownListener(ZmqContextShutdownListener listener) {
		shutdownListeners.add(listener);
	}
}
