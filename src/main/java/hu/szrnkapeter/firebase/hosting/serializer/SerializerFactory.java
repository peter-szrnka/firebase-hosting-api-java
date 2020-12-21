package hu.szrnkapeter.firebase.hosting.serializer;

import hu.szrnkapeter.firebase.hosting.type.SerializerType;

public class SerializerFactory {

	public static Serializer getSerializer(SerializerType type) {
		Serializer result = null;

		switch (type) {
		case GSON:
		case ORG_JSON:
		default:
			result = new JacksonSerializer();
			break;
		}

		return result;
	}
}