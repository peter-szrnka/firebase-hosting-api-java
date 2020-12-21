package io.github.szrnkapeter.firebase.hosting.serializer;

import java.util.Date;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

public class MoshiSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * @see hu.szrnkapeter.firebase.hosting.serializer.Serializer#getObject(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T getObject(Class<T> clazz, String responseBody) throws Exception {
		Moshi moshi = new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build();
		JsonAdapter<T> jsonAdapter = moshi.adapter(clazz);
		return jsonAdapter.fromJson(responseBody);
	}
}