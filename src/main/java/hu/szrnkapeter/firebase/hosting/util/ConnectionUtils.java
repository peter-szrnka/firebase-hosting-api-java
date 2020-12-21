package hu.szrnkapeter.firebase.hosting.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import hu.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;
import hu.szrnkapeter.firebase.hosting.serializer.SerializerFactory;

public class ConnectionUtils {
	
	private static final String FIREBASE_API_URL = "https://firebasehosting.googleapis.com/v1beta1/";
	private static final String CHARSET = "UTF-8";

	/**
	 * Opens an HTTP GET connection by the parameters.
	 * 
	 * @param config
	 * @param clazz
	 * @param accessToken
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static <T> T openHTTPGetConnection(FirebaseRestApiConfig config, Class<T> clazz, String accessToken, String url) throws Exception {
		URLConnection connection = new URL(FIREBASE_API_URL + url).openConnection();
		connection.setRequestProperty("Accept-Charset", CHARSET);
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return SerializerFactory.getSerializer(config.getSerializer()).getObject(clazz, responseBody);
		} finally {
			streamResponse.close();
		}
	}
}