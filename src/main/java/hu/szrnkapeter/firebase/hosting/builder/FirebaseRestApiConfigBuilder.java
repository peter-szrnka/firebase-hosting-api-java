package hu.szrnkapeter.firebase.hosting.builder;

import java.io.InputStream;

import hu.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;
import hu.szrnkapeter.firebase.hosting.type.SerializerType;

/**
 * Builder class to create {@link FirebaseRestApiConfig} instances.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class FirebaseRestApiConfigBuilder {
	
	private FirebaseRestApiConfig config;
	
	private FirebaseRestApiConfigBuilder() {
		config = new FirebaseRestApiConfig();
	}
	
	public static FirebaseRestApiConfigBuilder builder() {
		return new FirebaseRestApiConfigBuilder();
	}
	
	public FirebaseRestApiConfigBuilder withConfigStream(InputStream configStream) {
		config.setConfigStream(configStream);
		return this;
	}
	
	public FirebaseRestApiConfigBuilder withSiteName(String siteName) {
		config.setSiteName(siteName);
		return this;
	}
	
	public FirebaseRestApiConfigBuilder withSerializer(SerializerType serializer) {
		config.setSerializer(serializer);
		return this;
	}
	
	public FirebaseRestApiConfigBuilder withDefaultConnectionTimeout(int defaultConnectionTimeout) {
		config.setDefaultConnectionTimeout(defaultConnectionTimeout);
		return this;
	}
	
	public FirebaseRestApiConfigBuilder withDefaultReadTimeout(int defaultReadTimeout) {
		config.setDefaultReadTimeout(defaultReadTimeout);
		return this;
	}

	public FirebaseRestApiConfig build() {
		return config;
	}
}