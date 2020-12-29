package io.github.szrnkapeter.firebase.hosting.serializer;

import org.junit.Assert;
import org.junit.Test;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.type.SerializerType;

/**
 * Unittest of {@link SerializerFactory}
 */
public class SerializerFactoryTest {

	@Test
	public void test() {
		FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
				.withSerializer(SerializerType.GSON)
				.build();
		Serializer response = SerializerFactory.getSerializer(config);
		Assert.assertTrue(response instanceof GsonSerializer);
		
		// Test2
		config.setSerializer(SerializerType.MOSHI);
		response = SerializerFactory.getSerializer(config);
		Assert.assertTrue(response instanceof MoshiSerializer);
		
		// Test3
		config.setSerializer(SerializerType.JACKSON);
		response = SerializerFactory.getSerializer(config);
		Assert.assertTrue(response instanceof JacksonSerializer);
		
		// Test4
		config.setCustomSerializer(new MoshiSerializer());
		response = SerializerFactory.getSerializer(config);
		Assert.assertTrue(response instanceof MoshiSerializer);
	}
}