package io.github.szrnkapeter.firebase.hosting.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

class GoogleCredentialUtilsTest {
	
	private static MockedStatic<GoogleCredentials> mockedGoogleCredentials = mockStatic(GoogleCredentials.class);
	
	@Test
	void testPrivateConstructor() {
		assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(GoogleCredentialUtilsTest.class));
	}

	@Test
	void test01_GetAccessToken() throws Exception {
		final String mockAccessTokenValue = UUID.randomUUID().toString();
		AccessToken mockAccessToken = new AccessToken(mockAccessTokenValue, Date.from(Instant.now().plusSeconds(120)));
		GoogleCredentials mockGoogleCredentials = Mockito.mock(GoogleCredentials.class);
		
		Mockito.when(mockGoogleCredentials.createScoped(ArgumentMatchers.anyList())).thenReturn(mockGoogleCredentials);
		mockedGoogleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(mockGoogleCredentials);
		Mockito.when(mockGoogleCredentials.getAccessToken()).thenReturn(mockAccessToken);

		FirebaseHostingApiConfig config = new FirebaseHostingApiConfig();
		config.setConfigStream(new FileInputStream(new File("src/test/resources/test1.txt")));
		String response = GoogleCredentialUtils.getAccessToken(config);
		assertEquals(mockAccessTokenValue, response);
		
		mockedGoogleCredentials.close();
	}
}