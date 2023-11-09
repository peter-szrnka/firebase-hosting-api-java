package io.github.szrnkapeter.firebase.hosting.builder;

import java.io.InputStream;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.listener.HttpResponseListener;
import io.github.szrnkapeter.firebase.hosting.listener.ServiceResponseListener;
import io.github.szrnkapeter.firebase.hosting.serializer.Serializer;

/**
 * Builder class to create {@link FirebaseHostingApiConfig} instances.
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FirebaseHostingApiConfigBuilder {
	
	private final FirebaseHostingApiConfig config;
	
	private FirebaseHostingApiConfigBuilder() {
		config = new FirebaseHostingApiConfig();
	}
	
	public static FirebaseHostingApiConfigBuilder builder() {
		return new FirebaseHostingApiConfigBuilder();
	}
	
	public FirebaseHostingApiConfigBuilder withServiceAccountFileStream(InputStream serviceAccountFileStream) {
		config.setServiceAccountFileStream(serviceAccountFileStream);
		return this;
	}
	
	public FirebaseHostingApiConfigBuilder withSiteId(String siteId) {
		config.setSiteId(siteId);
		return this;
	}
	
	public FirebaseHostingApiConfigBuilder withDefaultConnectionTimeout(int defaultConnectionTimeout) {
		config.setDefaultConnectionTimeout(defaultConnectionTimeout);
		return this;
	}
	
	public FirebaseHostingApiConfigBuilder withDefaultReadTimeout(int defaultReadTimeout) {
		config.setDefaultReadTimeout(defaultReadTimeout);
		return this;
	}

	public FirebaseHostingApiConfigBuilder withHttpResponseListener(HttpResponseListener httpResponseListener) {
		config.setHttpResponseListener(httpResponseListener);
		return this;
	}
	
	public FirebaseHostingApiConfigBuilder withServiceResponseListener(ServiceResponseListener serviceResponseListener) {
		config.setServiceResponseListener(serviceResponseListener);
		return this;
	}
	
	public FirebaseHostingApiConfigBuilder withSerializer(Serializer serializer) {
		config.setSerializer(serializer);
		return this;
	}

	public FirebaseHostingApiConfig build() {
		return config;
	}
}