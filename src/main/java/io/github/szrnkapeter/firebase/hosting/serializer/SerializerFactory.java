package io.github.szrnkapeter.firebase.hosting.serializer;

import io.github.szrnkapeter.firebase.hosting.type.SerializerType;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class SerializerFactory {

	/**
	 * Creates a new {@link Serializer} instance.
	 * 
	 * @param type {@link SerializerType} enum value. 
	 * @return A new {@link Serializer} instance.
	 */
	public static Serializer getSerializer(SerializerType type) {
		Serializer result = null;

		switch (type) {
		case GSON:
			result = new GsonSerializer();
			break;
		case MOSHI:
			result = new MoshiSerializer();
			break;
		default:
			result = new JacksonSerializer();
			break;
		}

		return result;
	}
}