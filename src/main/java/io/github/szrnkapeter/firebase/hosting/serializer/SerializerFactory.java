package io.github.szrnkapeter.firebase.hosting.serializer;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * @author Peter Szrnka
 * @since 0.1
 * 
 * @deprecated Please use {@link FirebaseHostingApiConfig#getCustomSerializer()}
 */
public final class SerializerFactory {

	/**
	 * Creates a new {@link Serializer} instance.
	 * 
	 * @param config {@link FirebaseHostingApiConfig} instance.
	 * @return A new {@link Serializer} instance.
	 * 
	 * @deprecated Please use {@link FirebaseHostingApiConfig#getCustomSerializer()}
	 */
	public static Serializer getSerializer(FirebaseHostingApiConfig config) {
		if(config.getCustomSerializer() != null) {
			return config.getCustomSerializer();
		}

		switch (config.getSerializer()) {
		case GSON:
			return new GsonSerializer();
		case MOSHI:
			return new MoshiSerializer();
		default:
			return new JacksonSerializer();
		}
	}
	
	public static Serializer get(FirebaseHostingApiConfig config) {
		if(config.getCustomSerializer() == null) {
			throw new IllegalArgumentException("Custom serializer is missing from FirebaseHostingApiConfig!");
		}

		return config.getCustomSerializer();
	}
}