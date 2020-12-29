package io.github.szrnkapeter.firebase.hosting.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson based serializer/deserializer.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class JacksonSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) throws Exception {
		return getObjectMapper().readValue(obj, clazz);
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.szrnkapeter.firebase.hosting.serializer.Serializer#toJson(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String toJson(Class<T> clazz, T obj) throws Exception {
		return getObjectMapper().writeValueAsString(obj);
	}
	
	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		return objectMapper;
	}
}