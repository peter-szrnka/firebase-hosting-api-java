package io.github.szrnkapeter.firebase.hosting;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
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
import io.github.szrnkapeter.firebase.hosting.service.FileService;
import io.github.szrnkapeter.firebase.hosting.service.ReleaseService;
import io.github.szrnkapeter.firebase.hosting.service.VersionService;
import io.github.szrnkapeter.firebase.hosting.util.ConfigValidationUtils;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;
import io.github.szrnkapeter.firebase.hosting.util.VersionUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.github.szrnkapeter.firebase.hosting.util.TestConfigGenerator.getFirebaseRestApiConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseHostingApiClientTest {

	private static final String ACCESS_TOKEN = "TOKEN";
	private static final String VERSION = "1.0";

	private FirebaseHostingApiConfig config;
	private ReleaseService releaseService;
	private VersionService versionService;
	private FileService fileService;
	private FirebaseHostingApiClient client;

	@SneakyThrows
	void setup() {
		config = getFirebaseRestApiConfig();
		releaseService = mock(ReleaseService.class);
		versionService = mock(VersionService.class);
		fileService = mock(FileService.class);
		client = new FirebaseHostingApiClient(config, releaseService, versionService, fileService);
	}

	@Test
	void shouldCreateWithStaticMethod() {
		setup();

		try (MockedStatic<GoogleCredentialUtils> mockedGoogleCredentialUtils = mockStatic(GoogleCredentialUtils.class);
			 MockedStatic<ConfigValidationUtils> mockedConfigValidationUtil = mockStatic(ConfigValidationUtils.class)) {
			// arrange
			mockedGoogleCredentialUtils.when(() -> GoogleCredentialUtils.getAccessToken(any(FirebaseHostingApiConfig.class))).thenReturn(ACCESS_TOKEN);

			// act
			FirebaseHostingApiClient response = FirebaseHostingApiClient.newClient(config);

			// assert
			assertNotNull(response);
			mockedGoogleCredentialUtils.verify(() -> GoogleCredentialUtils.getAccessToken(any(FirebaseHostingApiConfig.class)));
			mockedConfigValidationUtil.verify(() -> ConfigValidationUtils.preValidateConfig(config));
		}
	}

	@Test
	@SneakyThrows
	void shouldCreateRelease() {
		try (MockedStatic<VersionUtils> mockedVersionUtils = mockStatic(VersionUtils.class)) {
			// arrange
			setup();
			Release mockResponse = new Release();
			when(releaseService.createRelease(VERSION)).thenReturn(mockResponse);
			mockedVersionUtils.when(() ->
					VersionUtils.getVersionName(any(FirebaseHostingApiConfig.class), eq(VERSION))).thenReturn(VERSION);

			// act
			Release response = client.createRelease(VERSION);

			// assert
			assertEquals(mockResponse, response);
			verify(releaseService).createRelease(VERSION);
		}
	}

	@Test
	@SneakyThrows
	void shouldShouldCreateVersion() {
		// arrange
		setup();
		Version mockVersion = new Version();
		when(versionService.createVersion()).thenReturn(mockVersion);

		// act
		Version response = client.createVersion();

		// assert
		assertEquals(mockVersion, response);
		verify(versionService).createVersion();
	}

	@Test
	@SneakyThrows
	void shouldDeleteVersion() {
		try (MockedStatic<VersionUtils> mockedVersionUtils = mockStatic(VersionUtils.class)) {
			// arrange
			setup();
			mockedVersionUtils.when(() ->
					VersionUtils.getVersionName(any(FirebaseHostingApiConfig.class), eq(VERSION))).thenReturn(VERSION);

			// act
			client.deleteVersion(VERSION);

			// assert
			verify(versionService).deleteVersion(VERSION);
		}
	}

	@Test
	@SneakyThrows
	void shouldShouldFinalizeVersion() {
		// arrange
		setup();
		Version mockVersion = new Version();
		when(versionService.finalizeVersion(VERSION)).thenReturn(mockVersion);

		// act
		Version response = client.finalizeVersion(VERSION);

		// assert
		assertEquals(mockVersion, response);
		verify(versionService).finalizeVersion(VERSION);
	}

	@Test
	@SneakyThrows
	void shouldGetReleases() {
		// arrange
		setup();
		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
				new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);
		when(releaseService.getReleases()).thenReturn(mockGetReleasesResponse);

		// act
		GetReleasesResponse response = client.getReleases();

		// assert
		assertNotNull(response);
		assertEquals(mockGetReleasesResponse, response);
	}

	@Test
	@SneakyThrows
	void shouldGetVersionFiles() {
		try (MockedStatic<VersionUtils> mockedVersionUtils = mockStatic(VersionUtils.class)) {
			// arrange
			setup();
			GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
			when(fileService.getVersionFiles(VERSION)).thenReturn(getVersionFilesResponse);
			mockedVersionUtils.when(() ->
					VersionUtils.getVersionName(any(FirebaseHostingApiConfig.class), eq(VERSION))).thenReturn(VERSION);

			// act
			GetVersionFilesResponse response = client.getVersionFiles(VERSION);

			// assert
			assertEquals(getVersionFilesResponse, response);
			verify(fileService).getVersionFiles(VERSION);
		}
	}

	@Test
	@SneakyThrows
	void shouldPopulateFiles() {
		// arrange
		setup();
		PopulateFilesRequest request = new PopulateFilesRequest();

		String randomHash = UUID.randomUUID().toString();
		PopulateFilesResponse mockResponse = new PopulateFilesResponse();
		List<String> mockHashes = new ArrayList<>();
		mockHashes.add(randomHash);
		mockResponse.setUploadRequiredHashes(mockHashes);
		when(fileService.populateFiles(request, VERSION)).thenReturn(mockResponse);

		// act
		PopulateFilesResponse response = client.populateFiles(request, VERSION);

		// assert
		assertEquals(mockResponse, response);
		verify(fileService).populateFiles(request, VERSION);
	}

	@Test
	@SneakyThrows
	void shouldUploadFile() {
		// arrange
		setup();
		UploadFileRequest request = new UploadFileRequest();

		// act
		client.uploadFile(request);

		// assert
		verify(fileService).uploadFile(request);
	}
	
	@Test
	@SneakyThrows
	void shouldCreateDeploy() {
		initCreateDeploy(true, false, false);
		initCreateDeploy(false, false, false);
		initCreateDeploy(false, true, false);
		initCreateDeploy(false, false, true);
	}

	private void initCreateDeploy(boolean cleanDeploy, boolean nullRelease, boolean nullResponseCallback) throws Exception {
		MockedStatic<FileUtils> mockedFileUtils = mockStatic(FileUtils.class);
		DeployRequest request = new DeployRequest();
		request.setCleanDeploy(cleanDeploy);
		Set<DeployItem> fileList = new HashSet<>();
		fileList.add(FirebaseHostingApiClientTestHelper.createDeployItem("test1.txt", "src/test/resources/test1.txt"));
		
		if(cleanDeploy) {
			fileList.add(FirebaseHostingApiClientTestHelper.createDeployItem("test2.txt", "src/test/resources/test2.txt"));
		}

		request.setFiles(fileList);

		FirebaseHostingApiConfig config = getFirebaseRestApiConfig();

		if (nullResponseCallback) {
			config.setHttpResponseCallback(null);
		}

		releaseService = mock(ReleaseService.class);
		versionService = mock(VersionService.class);
		fileService = mock(FileService.class);
		client = new FirebaseHostingApiClient(config, releaseService, versionService, fileService);

		// Mocking getReleases()
		ObjectMapper objectMapper = new ObjectMapper();
		GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
				new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

		when(releaseService.getReleases()).thenReturn(mockGetReleasesResponse);

		// Mocking getVersionFiles()
		GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
		List<FileDetails> mockFiles = new ArrayList<>();
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/x1.txt", "DEPLOYED"));
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/__/x2.txt", "DEPLOYED"));
		mockFiles.add(FirebaseHostingApiClientTestHelper.createFileDetails("/test2.txt", "DEPLOYED"));
		
		getVersionFilesResponse.setFiles(mockFiles);
		when(fileService.getVersionFiles(anyString())).thenReturn(getVersionFilesResponse);

		// Mocking createVersion()
		Version mockResponse = new Version();
		String randomName = UUID.randomUUID().toString();
		mockResponse.setName(randomName);

		when(versionService.createVersion()).thenReturn(mockResponse);

		// Mocking populateFiles()
		PopulateFilesResponse mockPopResponse = new PopulateFilesResponse();
		List<String> mockHashes = new ArrayList<>();
		mockHashes.add("asd1");
		mockPopResponse.setUploadRequiredHashes(mockHashes);

		when(fileService.populateFiles(any(PopulateFilesRequest.class), anyString())).thenReturn(mockPopResponse);

		// Mock finalizeVersion()
		String randomVersionName = UUID.randomUUID().toString();
		Version mockVersionResponse = new Version();
		mockResponse.setName(randomVersionName);
		when(versionService.finalizeVersion(VERSION)).thenReturn(mockVersionResponse);

		// Mocking fileUtils
		mockedFileUtils.when(() -> FileUtils.getRemoteFile(ArgumentMatchers.anyString())).thenReturn(new byte[0]);

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
			when(releaseService.getReleases()).thenReturn(mockGetReleasesResponse);
			response = client.createDeploy(request);
			assertNull(response);
			
			mockGetReleasesResponse = objectMapper.readValue(
					new File("src/test/resources/sample-response-get-releases3.json"), GetReleasesResponse.class);
			when(releaseService.getReleases()).thenReturn(mockGetReleasesResponse);

			response = client.createDeploy(request);
			assertNull(response);

			when(releaseService.getReleases()).thenReturn(null);
			response = client.createDeploy(request);
			assertNull(response);
		}
		mockedFileUtils.close();
	}
}