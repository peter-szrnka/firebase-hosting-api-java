package io.github.szrnkapeter.firebase.hosting;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesRequest;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.UploadFileRequest;
import io.github.szrnkapeter.firebase.hosting.model.Version;
import io.github.szrnkapeter.firebase.hosting.type.SerializerType;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ GoogleCredentialUtils.class, ConnectionUtils.class })
public class FirebaseHostingApiClientTest {

	private static final String ACCESS_TOKEN = "TOKEN";
	private static final String VERSION_NAME = "version1";

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GoogleCredentialUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
	}

	private FirebaseHostingApiConfig getFirebaseRestApiConfig() throws Exception {
		return FirebaseHostingApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("src/test/resources/test.json")).withSiteName("test")
				.withDefaultConnectionTimeout(60000).withDefaultReadTimeout(60000)
				.withSerializer(SerializerType.JACKSON).build();
	}

	@Test
	public void test001_GetReleases() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
				new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

		Mockito.when(ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockGetReleasesResponse);

		GetReleasesResponse response = client.getReleases();

		Assert.assertNotNull(response);
		Assert.assertEquals("XY012345", response.getNextPageToken());
		Assert.assertEquals(25, response.getReleases().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test002_GetVersionFiles_VersionNull() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		client.getVersionFiles(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test003_GetVersionFiles_VersionEmpty() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		client.getVersionFiles("");
	}

	@Test
	public void test004_GetVersionFiles_VersionEmpty() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
		Mockito.when(ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(getVersionFilesResponse);

		GetVersionFilesResponse response = client.getVersionFiles(VERSION_NAME);
		Assert.assertNotNull(response);

		response = client.getVersionFiles("sites/test/versions/72e01552bb5498a1");
		Assert.assertNotNull(response);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test005_BadConstructor() throws Exception {
		new FirebaseHostingApiClient(null);
	}

	@Test
	public void test006_CreateRelease() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		Release mockResponse = new Release();
		Mockito.when(ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.isNull())).thenReturn(mockResponse);

		Release response = client.createRelease(VERSION_NAME);
		Assert.assertNotNull(response);
	}

	@Test
	public void test007_CreateVersion() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomName = UUID.randomUUID().toString();
		Version mockResponse = new Version();
		mockResponse.setName(randomName);
		Mockito.when(ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(mockResponse);

		Version response = client.createVersion();
		Assert.assertNotNull(response);
		Assert.assertEquals(randomName, response.getName());
	}

	@Test
	public void test008_DeleteVersion() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		Mockito.when(ConnectionUtils.openSimpleHTTPConnection(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(FirebaseHostingApiConfig.class), ArgumentMatchers.any(Class.class),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.isNull()))
				.then(new Answer<Void>() {

					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						String version = invocation.getArgument(0);
						Assert.assertEquals(VERSION_NAME, version);
						return null;
					}
				});

		client.deleteVersion(VERSION_NAME);
	}

	@Test
	public void test009_FinalizeVersion() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomName = UUID.randomUUID().toString();
		Version mockResponse = new Version();
		mockResponse.setName(randomName);
		Mockito.when(ConnectionUtils.openSimpleHTTPConnection(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(FirebaseHostingApiConfig.class), ArgumentMatchers.any(Class.class),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockResponse);

		Version response = client.finalizeVersion(VERSION_NAME);
		Assert.assertNotNull(response);
		Assert.assertEquals(randomName, response.getName());
	}

	@Test
	public void test010_PopulateFiles() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
				.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomHash = UUID.randomUUID().toString();
		PopulateFilesResponse mockResponse = new PopulateFilesResponse();
		List<String> mockHashes = new ArrayList<>();
		mockHashes.add(randomHash);
		mockResponse.setUploadRequiredHashes(mockHashes);

		Mockito.when(ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(mockResponse);

		PopulateFilesRequest request = new PopulateFilesRequest();
		request.setFiles(new HashMap<String, String>());
		PopulateFilesResponse response = client.populateFiles(request, VERSION_NAME);
		Assert.assertNotNull(response);
		Assert.assertFalse(response.getUploadRequiredHashes().isEmpty());
	}

	@Test
	public void test011_UploadFile() throws Exception {
		Mockito.when(GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class)))
		.thenReturn(ACCESS_TOKEN);
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());
		
		UploadFileRequest request = new UploadFileRequest();
		request.setFileContent(Files.readAllBytes(new File("src/test/resources/test1.txt").toPath()));
		request.setVersion(VERSION_NAME);

		client.uploadFile(request);
		
		request.setUploadUrl("http://test.com");
		
		client.uploadFile(request);
	}
}