package io.github.szrnkapeter.firebase.hosting.config;

import java.io.InputStream;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.listener.HttpResponseListener;
import io.github.szrnkapeter.firebase.hosting.listener.ServiceResponseListener;
import io.github.szrnkapeter.firebase.hosting.serializer.Serializer;

/**
 * Configuration holder class. Please use {@link FirebaseHostingApiConfigBuilder}
 * to instantiate a new instance.
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FirebaseHostingApiConfig {

	private InputStream serviceAccountFileStream;
	private int defaultConnectionTimeout = 30000;
	private int defaultReadTimeout = 30000;
	private String siteId;
	private HttpResponseListener httpResponseListener;
	private ServiceResponseListener serviceResponseListener;
	private Serializer serializer;

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public InputStream getServiceAccountFileStream() {
		return serviceAccountFileStream;
	}

	public void setServiceAccountFileStream(InputStream serviceAccountFileStream) {
		this.serviceAccountFileStream = serviceAccountFileStream;
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

	public ServiceResponseListener getServiceResponseListener() {
		return serviceResponseListener;
	}

	public void setServiceResponseListener(ServiceResponseListener serviceResponseListener) {
		this.serviceResponseListener = serviceResponseListener;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FirebaseRestApiConfig [defaultConnectionTimeout="
				+ defaultConnectionTimeout + ", defaultReadTimeout=" + defaultReadTimeout
				+ ", siteId=" + siteId + "]";
	}
}