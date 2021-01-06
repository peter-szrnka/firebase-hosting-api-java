package io.github.szrnkapeter.firebase.hosting.serializer;

import java.util.Date;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * Moshi based serializer/deserializer.
 * 
 * @author Peter Szrnka
 * @since 0.1
 * 
 * @deprecated All internal {@link Serializer} implementation is obsolete, please implement your own {@link Serializer} and set it in {@link FirebaseHostingApiConfig#setCustomSerializer(Serializer)},
 */
public class MoshiSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) throws Exception {
		return getAdapter(clazz).fromJson(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.szrnkapeter.firebase.hosting.serializer.Serializer#toJson(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String toJson(Class<T> clazz, T obj) throws Exception {
		return getAdapter(clazz).toJson(obj);
	}
	
	private <T> JsonAdapter<T> getAdapter(Class<T> clazz) {
		Moshi moshi = new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build();
		return moshi.adapter(clazz);
	}
}