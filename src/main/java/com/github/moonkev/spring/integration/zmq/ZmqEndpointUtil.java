package com.github.moonkev.spring.integration.zmq;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.util.ReflectionUtils;
import org.zeromq.ZMQ;

import zmq.ZError;

public class ZmqEndpointUtil {

	
	public static int socketTypeFromName(String socketTypeName) {
		Field socketTypeField = ReflectionUtils.findField(ZMQ.class, socketTypeName);
		if (socketTypeField == null  || socketTypeField.getType() != int.class) {
			throw new BeanCreationException(String.format("%s is not a valid ZMQ socket type", socketTypeName));
		}
		return (Integer) ReflectionUtils.getField(socketTypeField, null);
	}
	
	public static MessageHandlingException buildMessageHandlingException(Message<?> failedMessage, int errno)  {
		if (errno == ZError.ETERM) {
			return new MessageHandlingException(failedMessage, "ZMQ Context has been terminated");
		} else if (errno == ZError.EAGAIN) {
			return new MessageHandlingException(failedMessage, "ZMQ socket timeout out while attempting to send or receive data over zmq socket");
		} else {
			return new MessageHandlingException(failedMessage, "Unknown error while attempting to send or receive data over zmq socket");
		}
	}
}
