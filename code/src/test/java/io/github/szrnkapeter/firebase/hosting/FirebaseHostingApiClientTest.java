package io.github.szrnkapeter.firebase.hosting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.listener.HttpResponseListener;
import io.github.szrnkapeter.firebase.hosting.listener.ServiceResponseListener;
import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;
import io.github.szrnkapeter.firebase.hosting.model.DeployResponse;
import io.github.szrnkapeter.firebase.hosting.model.FileDetails;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesRequest;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.UploadFileRequest;
import io.github.szrnkapeter.firebase.hosting.model.Version;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

@SuppressWarnings("unchecked")
class FirebaseHostingApiClientTest {

	private static final String SEPARATOR = " / ";
	private static final String ACCESS_TOKEN = "TOKEN";
	private static final String VERSION_NAME = "version1";
	private static int mockCounter = 0;
	
	private static final MockedStatic<GoogleCredentialUtils> mockedGoogleCredentialUtils = mockStatic(GoogleCredentialUtils.class);
	private static final MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class);
	private static final MockedStatic<FileUtils> mockedFileUtilsUtils = mockStatic(FileUtils.class);
	
	@BeforeEach
	public void setup() {
		mockedGoogleCredentialUtils.when(() -> GoogleCredentialUtils.getAccessToken(ArgumentMatchers.any(FirebaseHostingApiConfig.class))).thenReturn(ACCESS_TOKEN);
	}
	
	@AfterAll
	public static void tearAllDown() {
		mockedGoogleCredentialUtils.close();
		mockedConnectionUtilsUtils.close();
		mockedFileUtilsUtils.close();
	}

	private FirebaseHostingApiConfig getFirebaseRestApiConfig() throws Exception {
		return FirebaseHostingApiConfigBuilder.builder()
				.withServiceAccountFileStream(new FileInputStream("src/test/resources/test.json"))
				.withSiteId("test")
				.withSerializer(new GsonSerializer())
				.withDefaultConnectionTimeout(60000).withDefaultReadTimeout(60000)
				.withHttpResponseListener(new HttpResponseListener() {
					
					@Override
					public void getResponseInfo(String function, int code, String responseMessage) {
						System.out.println(function + SEPARATOR + code + SEPARATOR + responseMessage);
					}
				})
				.withServiceResponseListener(new ServiceResponseListener() {
					
					@Override
					public <T> void getResponse(String function, T response) {
						System.out.println(function + SEPARATOR + response);
					}
				})
				.build();
	}

	@Test
	void test001_GetReleases() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
				new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockGetReleasesResponse);

		GetReleasesResponse response = client.getReleases();

		assertNotNull(response);
		assertEquals("XY012345", response.getNextPageToken());
		assertEquals(25, response.getReleases().size());
	}

	@Test
	void test002_GetVersionFiles_VersionNull() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> client.getVersionFiles(null));
		
		assertEquals("Version field is mandatory!", exception.getMessage());
	}

	@Test
	void test003_GetVersionFiles_VersionEmpty() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> client.getVersionFiles(""));
		assertEquals("Version field is mandatory!", exception.getMessage());
	}

	@Test
	void test004_GetVersionFiles_VersionEmpty() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
		mockedConnectionUtilsUtils.when(() ->ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(getVersionFilesResponse);

		GetVersionFilesResponse response = client.getVersionFiles(VERSION_NAME);
		assertNotNull(response);

		response = client.getVersionFiles("sites/test/versions/72e01552bb5498a1");
		assertNotNull(response);
	}

	@Test
	void test005_BadConstructor() throws Exception {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FirebaseHostingApiClient(null));
		assertEquals("FirebaseRestApiConfig field is mandatory!", exception.getMessage());
	}

	@Test
	void test006_CreateRelease() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		Release mockResponse = new Release();
		mockedConnectionUtilsUtils.when(() ->ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.isNull(), ArgumentMatchers.anyString())).thenReturn(mockResponse);

		Release response = client.createRelease(VERSION_NAME);
		assertNotNull(response);
	}

	@Test
	void test007_CreateVersion() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomName = UUID.randomUUID().toString();
		Version mockResponse = new Version();
		mockResponse.setName(randomName);
		mockedConnectionUtilsUtils.when(() ->ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockResponse);

		Version response = client.createVersion();
		assertNotNull(response);
		assertEquals(randomName, response.getName());
	}

	@Test
	void test008_DeleteVersion() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		mockedConnectionUtilsUtils.when(() ->ConnectionUtils.openSimpleHTTPConnection(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(FirebaseHostingApiConfig.class), ArgumentMatchers.any(Class.class),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.isNull(), ArgumentMatchers.anyString()))
				.then(new Answer<Void>() {

					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						String version = invocation.getArgument(0);
						assertEquals(VERSION_NAME, version);
						return null;
					}
				});

		client.deleteVersion(VERSION_NAME);
	}

	@Test
	void test009_FinalizeVersion() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomName = UUID.randomUUID().toString();
		Version mockResponse = new Version();
		mockResponse.setName(randomName);
		mockedConnectionUtilsUtils.when(() ->ConnectionUtils.openSimpleHTTPConnection(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(FirebaseHostingApiConfig.class), ArgumentMatchers.any(Class.class),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockResponse);

		Version response = client.finalizeVersion(VERSION_NAME);
		assertNotNull(response);
		assertEquals(randomName, response.getName());
	}

	@Test
	void test010_PopulateFiles() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());

		String randomHash = UUID.randomUUID().toString();
		PopulateFilesResponse mockResponse = new PopulateFilesResponse();
		List<String> mockHashes = new ArrayList<>();
		mockHashes.add(randomHash);
		mockResponse.setUploadRequiredHashes(mockHashes);

		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockResponse);

		PopulateFilesRequest request = new PopulateFilesRequest();
		request.setFiles(new HashMap<String, String>());
		PopulateFilesResponse response = client.populateFiles(request, VERSION_NAME);
		assertNotNull(response);
		assertFalse(response.getUploadRequiredHashes().isEmpty());
	}
	
	@Test
	void test011_CreateDeploy() throws Exception {
		initCreateDeploy(true, false);
		initCreateDeploy(false, false);
		initCreateDeploy(false, true);
	}
	
	@Test
	void test012_UploadFile() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());
		
		mockCounter = 0;
		mockedFileUtilsUtils.when(() ->FileUtils.getSHA256Checksum(ArgumentMatchers.any(byte[].class))).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				mockCounter++;
				return "testSha";
			}
		});
		
		UploadFileRequest request = new UploadFileRequest();
		request.setFileContent("test".getBytes());
		request.setUploadUrl("http://www.test.com");
		client.uploadFile(request);
		
		assertEquals(1, mockCounter);
	}
	
	@Test
	void test013_GetVersionId_Fail1() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());
		
		String response = client.getVersionId(null);
		assertNull(response);
	}
	
	@Test
	void test013_GetVersionId_Fail2() throws Exception {
		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());
		
		String response = client.getVersionId("");
		assertNull(response);
	}

	@Test
	void test014_Init_Fail1_SiteIdNull() throws Exception {
		FirebaseHostingApiConfig invalidConfig = getFirebaseRestApiConfig();
		invalidConfig.setSiteId(null);

		// act & assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FirebaseHostingApiClient(invalidConfig));
		assertEquals("Site name is mandatory!", exception.getMessage());
	}

	@Test
	void test015_Init_Fail1_SiteIdEmpty() throws Exception {
		FirebaseHostingApiConfig invalidConfig = getFirebaseRestApiConfig();
		invalidConfig.setSiteId("");

		// act & assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FirebaseHostingApiClient(invalidConfig));
		assertEquals("Site name is mandatory!", exception.getMessage());
	}

	@Test
	void test016_Init_Fail1_ServiceAccountStreamNull() throws Exception {
		FirebaseHostingApiConfig invalidConfig = getFirebaseRestApiConfig();
		invalidConfig.setServiceAccountFileStream(null);

		// act & assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FirebaseHostingApiClient(invalidConfig));
		assertEquals("Service account file stream is missing from the configuration!", exception.getMessage());
	}

	@Test
	void test017_Init_Fail1_SiteIdEmpty() throws Exception {
		FirebaseHostingApiConfig invalidConfig = getFirebaseRestApiConfig();
		invalidConfig.setSerializer(null);

		// act & assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FirebaseHostingApiClient(invalidConfig));
		assertEquals("Serializer is missing from the configuration!", exception.getMessage());
	}

	private void initCreateDeploy(boolean cleanDeploy, boolean nullRelease) throws Exception {
		DeployRequest request = new DeployRequest();
		request.setCleanDeploy(cleanDeploy);
		Set<DeployItem> fileList = new HashSet<>();
		fileList.add(FirebaseHostingApiClientTestHelper.createDeployItem("test1.txt", "src/test/resources/test1.txt"));
		
		if(cleanDeploy) {
			fileList.add(FirebaseHostingApiClientTestHelper.createDeployItem("test2.txt", "src/test/resources/test2.txt"));
		}

		request.setFiles(fileList);

		FirebaseHostingApiClient client = new FirebaseHostingApiClient(getFirebaseRestApiConfig());
		
		// Mocking getReleases()
		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
				new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.eq(GetReleasesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockGetReleasesResponse);
		
		// Mocking getVersionFiles()
		GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
		List<FileDetails> mockFiles = new ArrayList<>();
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/x1.txt", "DEPLOYED"));
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/__/x2.txt", "DEPLOYED"));
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/test2.txt", "DEPLOYED"));
		
		getVersionFilesResponse.setFiles(mockFiles);
		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.eq(GetVersionFilesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(getVersionFilesResponse);
		
		// Mocking createVersion()
		Version mockResponse = new Version();
		String randomName = UUID.randomUUID().toString();
		mockResponse.setName(randomName);
		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.eq(Version.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockResponse);

		// Mocking populateFiles()
		PopulateFilesResponse mockPopResponse = new PopulateFilesResponse();
		List<String> mockHashes = new ArrayList<>();
		mockHashes.add("asd1");
		mockPopResponse.setUploadRequiredHashes(mockHashes);

		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
				ArgumentMatchers.eq(PopulateFilesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockPopResponse);

		// Mock finalizeVersion()
		String randomVersionName = UUID.randomUUID().toString();
		Version mockVersionResponse = new Version();
		mockResponse.setName(randomVersionName);
		mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPConnection(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(FirebaseHostingApiConfig.class), ArgumentMatchers.eq(Version.class),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(mockVersionResponse);
		
		// Mocking fileUtils
		Mockito.when(FileUtils.getRemoteFile(ArgumentMatchers.anyString())).thenReturn(new byte[0]);
		
		mockCounter = 0;
		
		byte[] mockCompressedFile = FileUtils.compressAndReadFile((Files.readAllBytes(new File("src/test/resources/test1.txt").toPath())));
		mockedFileUtilsUtils.when(() -> FileUtils.compressAndReadFile(ArgumentMatchers.any(byte[].class))).thenReturn(mockCompressedFile);
		mockedFileUtilsUtils.when(() -> FileUtils.getSHA256Checksum(mockCompressedFile)).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				mockCounter++;
				
				if(mockCounter % 2 == 1) {
					return "asd1";
				}

				return "asd2";
			}
		});

		// Service call
		DeployResponse response = client.createDeploy(request);
		assertNotNull(response);
		
		// Test2
		request.setDeletePreviousVersions(true);
		response = client.createDeploy(request);
		assertNotNull(response);

		if(nullRelease && !cleanDeploy) {
			mockGetReleasesResponse = objectMapper.readValue(
					new File("src/test/resources/sample-response-get-releases2.json"), GetReleasesResponse.class);
			mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
					ArgumentMatchers.eq(GetReleasesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
					.thenReturn(mockGetReleasesResponse);
			response = client.createDeploy(request);
			assertNull(response);
			
			mockGetReleasesResponse = objectMapper.readValue(
					new File("src/test/resources/sample-response-get-releases3.json"), GetReleasesResponse.class);
			mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
					ArgumentMatchers.eq(GetReleasesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
					.thenReturn(mockGetReleasesResponse);
			response = client.createDeploy(request);
			assertNull(response);
			
			mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(ArgumentMatchers.any(FirebaseHostingApiConfig.class),
					ArgumentMatchers.eq(GetReleasesResponse.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
					.thenReturn(null);
			response = client.createDeploy(request);
			assertNull(response);
		}
	}
}