package io.github.szrnkapeter.firebase.hosting.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ GoogleCredentials.class })
public class GoogleCredentialUtilsTest {
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(GoogleCredentials.class);
	}

	@Test
	public void test01_GetAccessToken() throws Exception {
		final String mockAccessTokenValue = UUID.randomUUID().toString();
		AccessToken mockAccessToken = new AccessToken(mockAccessTokenValue, Date.from(Instant.now().plusSeconds(120)));
		GoogleCredentials mockGoogleCredentials = PowerMockito.mock(GoogleCredentials.class);
		
		Mockito.when(mockGoogleCredentials.createScoped(ArgumentMatchers.anyList())).thenReturn(mockGoogleCredentials);
		Mockito.when(GoogleCredentials.fromStream(ArgumentMatchers.any(InputStream.class))).thenReturn(mockGoogleCredentials);
		Mockito.when(mockGoogleCredentials.getAccessToken()).thenReturn(mockAccessToken);

		FirebaseHostingApiConfig config = new FirebaseHostingApiConfig();
		config.setConfigStream(new FileInputStream(new File("src/test/resources/test1.txt")));
		String response = GoogleCredentialUtils.getAccessToken(config);
		Assert.assertEquals(mockAccessTokenValue, response);
	}
}