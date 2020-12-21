package io.github.szrnkapeter.firebase.hosting.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;
import io.github.szrnkapeter.firebase.hosting.serializer.SerializerFactory;

/**
 * Connection manager class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class ConnectionUtils {
	
	private static final String FIREBASE_API_URL = "https://firebasehosting.googleapis.com/v1beta1/";
	private static final String CHARSET = "UTF-8";

	/**
	 * Opens an HTTP GET connection by the parameters.
	 * 
	 * @param <T> T type
	 * @param config A {@link FirebaseRestApiConfig} object instance.
	 * @param clazz A class type
	 * @param accessToken Firebase access token
	 * @param url The input url that we want to call 
	 * @return The T type
	 * @throws Exception The unexpected exception
	 */
	public static <T> T openHTTPGetConnection(FirebaseRestApiConfig config, Class<T> clazz, String accessToken, String url) throws Exception {
		URLConnection connection = new URL(FIREBASE_API_URL + url).openConnection();
		connection.setRequestProperty("Accept-Charset", CHARSET);
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setConnectTimeout(config.getDefaultConnectionTimeout());
		connection.setReadTimeout(config.getDefaultReadTimeout());

		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return SerializerFactory.getSerializer(config.getSerializer()).getObject(clazz, responseBody);
		} finally {
			streamResponse.close();
		}
	}
}