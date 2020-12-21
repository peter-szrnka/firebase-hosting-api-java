package hu.szrnkapeter.firebase.hosting.util;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import hu.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;

public class GoogleCredentialUtils {
	
	private static final String FIREBASE_DEFAULT_SCOPE = "https://www.googleapis.com/auth/firebase";

	/**
	 * Returns with the access token by the Firebase credential JSON.
	 * 
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public static String getAccessToken(FirebaseRestApiConfig config) throws IOException {
		GoogleCredential googleCredential = GoogleCredential.fromStream(config.getConfigStream())
				.createScoped(Arrays.asList(FIREBASE_DEFAULT_SCOPE));
		googleCredential.refreshToken();
		return googleCredential.getAccessToken();
	}
}
