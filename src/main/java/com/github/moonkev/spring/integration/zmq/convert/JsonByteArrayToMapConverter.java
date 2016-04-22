package com.github.moonkev.spring.integration.zmq.convert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class JsonByteArrayToMapConverter implements Converter<byte[], Map<Object, Object>> {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	public Map<Object, Object> convert(byte[] bytes) {

		try {
			return mapper.readValue(bytes, new TypeReference<HashMap<String, String>>(){});
		} catch (IOException e) {
			throw new ConversionFailedException(
					TypeDescriptor.valueOf(byte[].class), 
					TypeDescriptor.valueOf(Map.class), 
					bytes, e);
		}
	}
}
