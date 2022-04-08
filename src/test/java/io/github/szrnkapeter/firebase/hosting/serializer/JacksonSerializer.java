package io.github.szrnkapeter.firebase.hosting.serializer;

import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson based serializer/deserializer.
 * 
 * @author Peter Szrnka
 * @since 0.6
 * 
 */
public class JacksonSerializer implements Serializer {
	
	private static final Logger logger = Logger.getLogger(JacksonSerializer.class.getName());  

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) {
		try {
			return getObjectMapper().readValue(obj, clazz);
		} catch (JsonProcessingException e) {
			logger.warning("String cannot be converted to object! " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.szrnkapeter.firebase.hosting.serializer.Serializer#toJson(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String toJson(Class<T> clazz, T obj) {
		try {
			return getObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.warning("Object cannot be converted to string! " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		return objectMapper;
	}
}