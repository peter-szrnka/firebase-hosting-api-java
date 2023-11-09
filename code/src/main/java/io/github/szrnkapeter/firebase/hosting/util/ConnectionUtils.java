package io.github.szrnkapeter.firebase.hosting.util;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * Connection manager class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class ConnectionUtils {

	private ConnectionUtils() {
	}

	/**
	 * Opens an HTTP GET connection by the parameters.
	 * 
	 * @param <T>         T type
	 * @param config      A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz       A class type
	 * @param accessToken Firebase access token
	 * @param url         The input url that we want to call
	 * @return The T type
	 * @throws IOException The unexpected exception
	 */
	public static <T> T openHTTPGetConnection(FirebaseHostingApiConfig config, Class<T> clazz, String accessToken,
			String url) throws IOException {
		URLConnection connection = initURLConnection(config, Constants.FIREBASE_API_URL + url, accessToken,
				"application/json");
		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return config.getSerializer().getObject(clazz, responseBody);
		} finally {
			streamResponse.close();
		}
	}

	/**
	 * Opens an HTTP connection by the parameters. Based on
	 * {@link #openSimpleHTTPConnection(String, FirebaseHostingApiConfig, Class, String, String, String, String)}
	 * 
	 * @param <T>           T type
	 * @param requestMethod Request method: POST | PATCH
	 * @param config        A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz         A class type
	 * @param accessToken   Firebase access token
	 * @param url           The input url that we want to call
	 * @param data          The input data
	 * @param function      The called functions name.
	 * @return The T type
	 * @throws IOException The unexpected exception
	 * 
	 * @since 0.2
	 */
	public static <T> T openSimpleHTTPConnection(String requestMethod, FirebaseHostingApiConfig config, Class<T> clazz,
			String accessToken, String url, String data, String function) throws IOException {
		return openSimpleHTTPConnection(
				SimpleHttpConnectionInput.builder()
				.withRequestMethod(requestMethod)
				.withConfig(config)
				.withClazz(clazz)
				.withAccessToken(accessToken)
				.withUrl(url)
				.withData(data)
				.withContentType("application/json")
				.withFunction(function)
				.build());
	}

	private static <T> T openSimpleHTTPConnection(SimpleHttpConnectionInput input) throws IOException {
		byte[] postData = input.getData() == null ? new byte[0] : input.getData().getBytes();

		HttpURLConnection connection = (HttpURLConnection) initURLConnection(input.getConfig(), Constants.FIREBASE_API_URL + input.getUrl(), input.getAccessToken(), input.getContentType());
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("charset", Constants.CHARSET);
		connection.setRequestProperty("Content-Length", String.valueOf(postData.length));

		if (Constants.PATCH.equals(input.getRequestMethod())) {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("X-HTTP-Method-Override", Constants.PATCH);
		} else {
			connection.setRequestMethod(input.getRequestMethod());
		}

		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			wr.write(postData);
		}

		if (input.getConfig().getHttpResponseListener() != null) {
			input.getConfig().getHttpResponseListener().getResponseInfo(input.getFunction(), connection.getResponseCode(),
					connection.getResponseMessage());
		}

		InputStream streamResponse = connection.getInputStream();

		try (Scanner scanner = new Scanner(streamResponse)) {
			String responseBody = scanner.useDelimiter("\\A").next();
			return input.getClazz() == null ? null : (T) input.getConfig().getSerializer().getObject(input.getClazz(), responseBody);
		} finally {
			streamResponse.close();
		}
	}

	/**
	 * Calls
	 * {@link #openSimpleHTTPConnection(String, FirebaseHostingApiConfig, Class, String, String, String, String, String)}
	 * method with POST parameter.
	 * 
	 * @param <T>         T type
	 * @param config      A {@link FirebaseHostingApiConfig} object instance.
	 * @param clazz       A class type
	 * @param accessToken Firebase access token
	 * @param url         The input url that we want to call
	 * @param data        The input data
	 * @param function    The called functions name.
	 * @return The T type
	 * @throws IOException The unexpected exception
	 */
	public static <T> T openSimpleHTTPPostConnection(FirebaseHostingApiConfig config, Class<T> clazz,
			String accessToken, String url, String data, String function) throws IOException {
		return openSimpleHTTPConnection(Constants.POST, config, clazz, accessToken, url, data, function);
	}

	private static URLConnection initURLConnection(FirebaseHostingApiConfig config, String url, String accessToken,
			String contentType) throws IOException {
		URLConnection connection = new URL(url).openConnection();

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
	 * @param url         Remote URL
	 * @param fileContent File content in byte array.
	 * @throws IOException The unexpected exception
	 * 
	 * @since 0.2
	 */
	public static void uploadFile(FirebaseHostingApiConfig config, String accessToken, String fileName, String url,
			byte[] fileContent) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) initURLConnection(config, url, accessToken,
				"application/octet-stream");
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
		connection.connect();
		bis.close();
		request.flush();
		request.close();

		if (config.getHttpResponseListener() != null) {
			config.getHttpResponseListener().getResponseInfo("uploadFile", connection.getResponseCode(),
					"File: " + fileName + " / message: " + connection.getResponseMessage());
		}
	}
}