package io.github.szrnkapeter.firebase.hosting.serializer;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class SerializerFactory {

	/**
	 * Creates a new {@link Serializer} instance.
	 * 
	 * @param config {@link FirebaseHostingApiConfig} instance.
	 * @return A new {@link Serializer} instance.
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
}