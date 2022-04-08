package io.github.szrnkapeter.firebase.hosting.util;

import javax.annotation.processing.Generated;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

public class SimpleHttpConnectionInput {

	private String requestMethod;
	private FirebaseHostingApiConfig config;
	private Class<?> clazz;
	private String accessToken;
	private String url;
	private String data;
	private String contentType;
	private String function;

	private SimpleHttpConnectionInput(Builder builder) {
		this.requestMethod = builder.requestMethod;
		this.config = builder.config;
		this.clazz = builder.clazz;
		this.accessToken = builder.accessToken;
		this.url = builder.url;
		this.data = builder.data;
		this.contentType = builder.contentType;
		this.function = builder.function;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public FirebaseHostingApiConfig getConfig() {
		return config;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getUrl() {
		return url;
	}

	public String getData() {
		return data;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFunction() {
		return function;
	}

	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	@Generated("SparkTools")
	public static final class Builder {
		private String requestMethod;
		private FirebaseHostingApiConfig config;
		private Class<?> clazz;
		private String accessToken;
		private String url;
		private String data;
		private String contentType;
		private String function;

		private Builder() {
		}

		public Builder withRequestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}

		public Builder withConfig(FirebaseHostingApiConfig config) {
			this.config = config;
			return this;
		}

		public Builder withClazz(Class<?> clazz) {
			this.clazz = clazz;
			return this;
		}

		public Builder withAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder withData(String data) {
			this.data = data;
			return this;
		}

		public Builder withContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder withFunction(String function) {
			this.function = function;
			return this;
		}

		public SimpleHttpConnectionInput build() {
			return new SimpleHttpConnectionInput(this);
		}
	}
}
