package io.github.szrnkapeter.firebase.hosting.serializer;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

/**
 * Moshi based serializer/deserializer.
 * 
 * @author Peter Szrnka
 * @since 0.6
 * 
 */
public class MoshiSerializer implements Serializer {
	
	private static final Logger logger = Logger.getLogger(MoshiSerializer.class.getName());  

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) {
		try {
			return getAdapter(clazz).fromJson(obj);
		} catch (IOException e) {
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
		return getAdapter(clazz).toJson(obj);
	}
	
	private <T> JsonAdapter<T> getAdapter(Class<T> clazz) {
		Moshi moshi = new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build();
		return moshi.adapter(clazz);
	}
}