package io.github.szrnkapeter.firebase.hosting.type;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.serializer.Serializer;

/**
 * @author Peter Szrnka
 * @since 0.1
 * 
 * @deprecatedAll internal {@link Serializer} implementation is obsolete, please implement your own {@link Serializer} and set it in {@link FirebaseHostingApiConfig#setCustomSerializer(Serializer)},
 */
public enum SerializerType {

	GSON,
	JACKSON,
	MOSHI;
}