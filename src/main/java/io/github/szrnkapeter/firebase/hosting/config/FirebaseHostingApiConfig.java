package io.github.szrnkapeter.firebase.hosting.config;

import java.io.InputStream;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.listener.HttpResponseListener;
import io.github.szrnkapeter.firebase.hosting.listener.ServiceResponseListener;
import io.github.szrnkapeter.firebase.hosting.type.SerializerType;

/**
 * Configuration holder class. Please use {@link FirebaseHostingApiConfigBuilder}
 * to instantiate a new instance.
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FirebaseHostingApiConfig {

	private InputStream configStream;
	private int defaultConnectionTimeout = 30000;
	private int defaultReadTimeout = 30000;
	private SerializerType serializer = SerializerType.JACKSON;
	private String siteName;
	private HttpResponseListener httpResponseListener;
	private ServiceResponseListener serviceResponnseListener;

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public InputStream getConfigStream() {
		return configStream;
	}

	public void setConfigStream(InputStream configStream) {
		this.configStream = configStream;
	}

	public SerializerType getSerializer() {
		return serializer;
	}

	public void setSerializer(SerializerType serializer) {
		this.serializer = serializer;
	}

	public int getDefaultConnectionTimeout() {
		return defaultConnectionTimeout;
	}

	public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
		this.defaultConnectionTimeout = defaultConnectionTimeout;
	}

	public int getDefaultReadTimeout() {
		return defaultReadTimeout;
	}

	public void setDefaultReadTimeout(int defaultReadTimeout) {
		this.defaultReadTimeout = defaultReadTimeout;
	}

	public HttpResponseListener getHttpResponseListener() {
		return httpResponseListener;
	}

	public void setHttpResponseListener(HttpResponseListener httpResponseListener) {
		this.httpResponseListener = httpResponseListener;
	}

	public ServiceResponseListener getServiceResponnseListener() {
		return serviceResponnseListener;
	}

	public void setServiceResponnseListener(ServiceResponseListener serviceResponnseListener) {
		this.serviceResponnseListener = serviceResponnseListener;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FirebaseRestApiConfig [configStream=" + configStream + ", defaultConnectionTimeout="
				+ defaultConnectionTimeout + ", defaultReadTimeout=" + defaultReadTimeout + ", serializer=" + serializer
				+ ", siteName=" + siteName + "]";
	}
}