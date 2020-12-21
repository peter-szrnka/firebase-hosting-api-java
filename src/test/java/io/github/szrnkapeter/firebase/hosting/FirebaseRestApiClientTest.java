package io.github.szrnkapeter.firebase.hosting;

import java.io.File;
import java.io.FileInputStream;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.szrnkapeter.firebase.hosting.FirebaseRestApiClient;
import io.github.szrnkapeter.firebase.hosting.builder.FirebaseRestApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.type.SerializerType;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ GoogleCredentialUtils.class, ConnectionUtils.class })
public class FirebaseRestApiClientTest {

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GoogleCredentialUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
	}

	private FirebaseRestApiConfig getFirebaseRestApiConfig() throws Exception {
		return FirebaseRestApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("src/test/resources/test.json")).withSiteName("test")
				.withSerializer(SerializerType.JACKSON).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test001_GetReleases() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseRestApiConfig.class)))
				.thenReturn("TOKEN");
		FirebaseRestApiClient client = new FirebaseRestApiClient(getFirebaseRestApiConfig());

		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

		Mockito.when(ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseRestApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockGetReleasesResponse);

		GetReleasesResponse response = client.getReleases();

		Assert.assertNotNull(response);
		Assert.assertEquals("XY012345", response.getNextPageToken());
		Assert.assertEquals(25, response.getReleases().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test002_GetVersionFiles_VersionNull() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseRestApiConfig.class)))
				.thenReturn("TOKEN");
		FirebaseRestApiClient client = new FirebaseRestApiClient(getFirebaseRestApiConfig());

		client.getVersionFiles(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test003_GetVersionFiles_VersionEmpty() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseRestApiConfig.class)))
				.thenReturn("TOKEN");
		FirebaseRestApiClient client = new FirebaseRestApiClient(getFirebaseRestApiConfig());

		client.getVersionFiles("");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test004_GetVersionFiles_VersionEmpty() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseRestApiConfig.class)))
				.thenReturn("TOKEN");
		FirebaseRestApiClient client = new FirebaseRestApiClient(getFirebaseRestApiConfig());

		GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
		Mockito.when(ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseRestApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(getVersionFilesResponse);

		GetVersionFilesResponse response = client.getVersionFiles("version1");
		Assert.assertNotNull(response);

		response = client.getVersionFiles("sites/test/versions/72e01552bb5498a1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test005_BadConstructor() throws Exception {
		new FirebaseRestApiClient(null);
	}
}
