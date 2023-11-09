package io.github.szrnkapeter.firebase.hosting.util;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class GoogleCredentialUtilsTest {
	
	private MockedStatic<GoogleCredentials> mockedGoogleCredentials;
	
	@Test
	void testPrivateConstructor() {
		assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(GoogleCredentialUtilsTest.class));
	}

	@Test
	void test01_GetAccessToken() throws Exception {
		mockedGoogleCredentials = mockStatic(GoogleCredentials.class);
		final String mockAccessTokenValue = UUID.randomUUID().toString();
		AccessToken mockAccessToken = new AccessToken(mockAccessTokenValue, Date.from(Instant.now().plusSeconds(120)));
		GoogleCredentials mockGoogleCredentials = Mockito.mock(GoogleCredentials.class);
		
		Mockito.when(mockGoogleCredentials.createScoped(ArgumentMatchers.anyList())).thenReturn(mockGoogleCredentials);
		mockedGoogleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(mockGoogleCredentials);
		Mockito.when(mockGoogleCredentials.getAccessToken()).thenReturn(mockAccessToken);

		FirebaseHostingApiConfig config = new FirebaseHostingApiConfig();
		config.setServiceAccountFileStream(new FileInputStream(new File("src/test/resources/test1.txt")));
		String response = GoogleCredentialUtils.getAccessToken(config);
		assertEquals(mockAccessTokenValue, response);

		mockedGoogleCredentials.close();
	}

	@Test
	void test02_GetAccessTokenWithFailure() throws Exception {
		mockedGoogleCredentials = mockStatic(GoogleCredentials.class);
		GoogleCredentials mockGoogleCredentials = Mockito.mock(GoogleCredentials.class);

		Mockito.when(mockGoogleCredentials.createScoped(ArgumentMatchers.anyList())).thenReturn(mockGoogleCredentials);
		mockedGoogleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenThrow(new IllegalArgumentException("Invalid!"));

		FirebaseHostingApiConfig config = new FirebaseHostingApiConfig();
		config.setServiceAccountFileStream(new FileInputStream(new File("src/test/resources/test1.txt")));
		RuntimeException failure = assertThrows(RuntimeException.class, () -> GoogleCredentialUtils.getAccessToken(config));

		assertEquals("Unexpected exception occurred after parsing the service account JSON file! Please check that the file is valid!", failure.getMessage());
		assertEquals(IllegalArgumentException.class, failure.getCause().getClass());

		mockedGoogleCredentials.close();
	}
}