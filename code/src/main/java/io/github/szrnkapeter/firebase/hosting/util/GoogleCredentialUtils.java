package io.github.szrnkapeter.firebase.hosting.util;

import com.google.auth.oauth2.GoogleCredentials;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

import java.util.List;

/**
 * Google credential helper class.
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class GoogleCredentialUtils {
	
	private static final String FIREBASE_DEFAULT_SCOPE = "https://www.googleapis.com/auth/firebase";
	
	private GoogleCredentialUtils() {
	}

	/**
	 * Returns with the access token by the Firebase credential JSON.
	 * 
	 * @param config A {@link FirebaseHostingApiConfig} instance
	 * @return The queried access token.
	 */
	public static String getAccessToken(FirebaseHostingApiConfig config) {
		try {
			GoogleCredentials googleCredential = GoogleCredentials.fromStream(config.getServiceAccountFileStream()).createScoped(List.of(FIREBASE_DEFAULT_SCOPE));
			googleCredential.refreshIfExpired();
			return googleCredential.getAccessToken().getTokenValue();
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception occurred after parsing the service account JSON file! Please check that the file is valid!", e);
		}
	}
}