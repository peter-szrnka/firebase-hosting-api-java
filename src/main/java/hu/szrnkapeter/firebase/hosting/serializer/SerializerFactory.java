package hu.szrnkapeter.firebase.hosting.serializer;

import hu.szrnkapeter.firebase.hosting.type.SerializerType;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class SerializerFactory {

	/**
	 * Creates a new {@link Serializer} instance.
	 * 
	 * @param type
	 * @return
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