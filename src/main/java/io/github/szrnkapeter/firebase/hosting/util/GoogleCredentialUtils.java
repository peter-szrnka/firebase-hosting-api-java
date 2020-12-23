package io.github.szrnkapeter.firebase.hosting.util;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * Google credential helper class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class GoogleCredentialUtils {
	
	private static final String FIREBASE_DEFAULT_SCOPE = "https://www.googleapis.com/auth/firebase";

	/**
	 * Returns with the access token by the Firebase credential JSON.
	 * 
	 * @param config A {@link FirebaseHostingApiConfig} instance
	 * @return The queried access token.
	 * @throws IOException Any IO exception.
	 */
	public static String getAccessToken(FirebaseHostingApiConfig config) throws IOException {
		// TODO Refactor this to use the newest Google client API library.
		GoogleCredential googleCredential = GoogleCredential.fromStream(config.getConfigStream())
				.createScoped(Arrays.asList(FIREBASE_DEFAULT_SCOPE));
		googleCredential.refreshToken();
		return googleCredential.getAccessToken();
	}
}