package io.github.szrnkapeter.firebase.hosting.serializer;

import com.google.gson.Gson;

/**
 * Gson based serializer/deserializer example.
 * 
 * @author Peter Szrnka
 * @since 0.6
 */
public class GsonSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) {
		return new Gson().fromJson(obj, clazz);
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.szrnkapeter.firebase.hosting.serializer.Serializer#toJson(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String toJson(Class<T> clazz, T obj) {
		return new Gson().toJson(obj);
	}
}