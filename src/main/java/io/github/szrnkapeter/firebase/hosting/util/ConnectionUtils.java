package io.github.szrnkapeter.firebase.hosting.util;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.serializer.SerializerFactory;

/**
 * Connection manager class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class ConnectionUtils {

	/**
	 * Opens an HTTP GET connection by the parameters.
	 * 
	 * @param             <T> T type
	 * @param config      A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz       A class type
	 * @param accessToken Firebase access token
	 * @param url         The input url that we want to call
	 * @return The T type
	 * @throws Exception The unexpected exception
	 */
	public static <T> T openHTTPGetConnection(FirebaseHostingApiConfig config, Class<T> clazz, String accessToken,
			String url) throws Exception {
		URLConnection connection = new URL(Constants.FIREBASE_API_URL + url).openConnection();
		connection = initURLConnection(config, connection, accessToken, "application/json");
		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return SerializerFactory.getSerializer(config.getSerializer()).getObject(clazz, responseBody);
		} finally {
			streamResponse.close();
		}
	}

	/**
	 * Opens an HTTP connection by the parameters. Based on
	 * {@link #openSimpleHTTPConnection(String, FirebaseHostingApiConfig, Class, String, String, String, String)}
	 * 
	 * @param               <T> T type
	 * @param requestMethod Request method: POST | PATCH
	 * @param config        A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz         A class type
	 * @param accessToken   Firebase access token
	 * @param url           The input url that we want to call
	 * @param data          The input data
	 * @return The T type
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public static <T> T openSimpleHTTPConnection(String requestMethod, FirebaseHostingApiConfig config, Class<T> clazz,
			String accessToken, String url, String data) throws Exception {
		return openSimpleHTTPConnection(requestMethod, config, clazz, accessToken, url, data, "application/json");
	}

	private static <T> T openSimpleHTTPConnection(String requestMethod, FirebaseHostingApiConfig config, Class<T> clazz,
			String accessToken, String url, String data, String contentType) throws Exception {
		byte[] postData = data == null ? new byte[0] : data.getBytes();

		HttpURLConnection connection = (HttpURLConnection) new URL(Constants.FIREBASE_API_URL + url).openConnection();
		connection = (HttpURLConnection) initURLConnection(config, connection, accessToken, contentType);
		connection.setDoOutput(true);
		// connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);
		// connection.setRequestProperty("charset", Constants.CHARSET);
		connection.setRequestProperty("Content-Length", String.valueOf(postData.length));

		if (Constants.PATCH.equals(requestMethod)) {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("X-HTTP-Method-Override", Constants.PATCH);
		} else {
			connection.setRequestMethod(requestMethod);
		}

		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			wr.write(postData);
		}

		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return clazz == null ? null
					: SerializerFactory.getSerializer(config.getSerializer()).getObject(clazz, responseBody);
		} finally {
			streamResponse.close();
		}
	}

	/**
	 * Calls
	 * {@link #openSimpleHTTPConnection(String, FirebaseHostingApiConfig, Class, String, String, String)}
	 * method with POST parameter.
	 * 
	 * @param             <T> T type
	 * @param config      A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz       A class type
	 * @param accessToken Firebase access token
	 * @param url         The input url that we want to call
	 * @param data        The input data
	 * @return The T type
	 * @throws Exception The unexpected exception
	 */
	public static <T> T openSimpleHTTPPostConnection(FirebaseHostingApiConfig config, Class<T> clazz, String accessToken,
			String url, String data) throws Exception {
		return openSimpleHTTPConnection(Constants.POST, config, clazz, accessToken, url, data);
	}

	private static URLConnection initURLConnection(FirebaseHostingApiConfig config, URLConnection connection,
			String accessToken, String contentType) {
		connection.setRequestProperty("Accept-Charset", Constants.CHARSET);
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setRequestProperty("Content-Type", contentType);
		connection.setConnectTimeout(config.getDefaultConnectionTimeout());
		connection.setReadTimeout(config.getDefaultReadTimeout());
		return connection;
	}

	/**
	 * Uploads the given file to the remote servers.
	 * 
	 * @param config      A {@link FirebaseHostingApiConfig} object instance.
	 * @param accessToken Firebase access token
	 * @param url Remote URL
	 * @param fileContent File content in byte array.
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public static void uploadFile(FirebaseHostingApiConfig config, String accessToken, String url, byte[] fileContent) throws Exception {

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection = (HttpURLConnection) initURLConnection(config, connection, accessToken, "application/octet-stream");
		connection.setRequestProperty("Content-Length", "" + fileContent.length);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");

		OutputStream outputStream = connection.getOutputStream();
		DataOutputStream request = new DataOutputStream(outputStream);

		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileContent);

		while ((bytesRead = bis.read(buffer)) != -1) {
			request.write(buffer, 0, bytesRead);
		}

		bis.close();
		request.flush();
		request.close();
		connection.connect();
	}
}