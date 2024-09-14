package hu.szrnkapeter.sample;

import com.google.gson.Gson;

import io.github.szrnkapeter.firebase.hosting.serializer.Serializer;

public class GsonSerializer implements Serializer {

	private final Gson gson;

	public GsonSerializer() {
		gson = new Gson();
	}

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String obj) {
		return gson.fromJson(obj, clazz);
	}

	/*
	 * (non-Javadoc)
	 * @see io.github.szrnkapeter.firebase.hosting.serializer.Serializer#toJson(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> String toJson(Class<T> clazz, T obj) {
		return gson.toJson(obj);
	}
}