package io.github.szrnkapeter.firebase.hosting.serializer;

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
	public <T> T getObject(Class<T> clazz, String responseBody) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(responseBody, clazz);
	}
}