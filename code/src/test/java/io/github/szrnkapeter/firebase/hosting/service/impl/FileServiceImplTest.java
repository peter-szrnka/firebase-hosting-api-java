package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.*;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
class FileServiceImplTest {

    private static final String VERSION_NAME = "version1";

    private FirebaseHostingApiConfig config;
    private FileServiceImpl service;

    @BeforeEach
    void setup() {
        config = new FirebaseHostingApiConfig();
        config.setSiteId("test");
        config.setDisableAsync(false);
        service = new FileServiceImpl(config, "accessToken");
    }

    @Test
    @SneakyThrows
    void shouldGetVersionFiles() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            GetVersionFilesResponse getVersionFilesResponse = new GetVersionFilesResponse();
            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(any(FirebaseHostingApiConfig.class),
                            eq(GetVersionFilesResponse.class), anyString(), anyString()))
                    .thenReturn(getVersionFilesResponse);

            // act
            GetVersionFilesResponse response = service.getVersionFiles(VERSION_NAME);

            // assert
            assertNotNull(response);
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openHTTPGetConnection(any(FirebaseHostingApiConfig.class),
                    eq(GetVersionFilesResponse.class), anyString(), anyString()));
        }
    }

    @Test
    @SneakyThrows
    void shouldPopulateFiles() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            String randomHash = UUID.randomUUID().toString();
            PopulateFilesResponse mockResponse = new PopulateFilesResponse();
            List<String> mockHashes = new ArrayList<>();
            mockHashes.add(randomHash);
            mockResponse.setUploadRequiredHashes(mockHashes);

            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    eq(PopulateFilesResponse.class), anyString(), anyString(),
                    anyString(), anyString())).thenReturn(mockResponse);

            PopulateFilesRequest request = new PopulateFilesRequest();
            request.setFiles(new HashMap<>());

            config.setSerializer(new GsonSerializer());

            // act
            PopulateFilesResponse response = service.populateFiles(request, VERSION_NAME);

            // assert
            assertNotNull(response);
            assertFalse(response.getUploadRequiredHashes().isEmpty());

            ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    eq(PopulateFilesResponse.class), anyString(), anyString(), dataCaptor.capture(), anyString()));

            assertEquals("{\"files\":{}}", dataCaptor.getValue());
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("uploadFileData")
    void shouldUploadOneFile(String uploadUrl, String expectedUrl) {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            UploadFileRequest request = new UploadFileRequest();
            request.setFileName("file1.txt");
            request.setFileContent("test1".getBytes());
            request.setVersion("1.0");
            request.setUploadUrl(uploadUrl);

            // act
            service.uploadFile(request);

            // assert
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            mockedConnectionUtilsUtils.verify(() ->
                    ConnectionUtils.uploadFile(eq(config), eq("accessToken"), anyString(), urlCaptor.capture(), any()));
            assertEquals(expectedUrl, urlCaptor.getValue());
        }
    }

    @Test
    void shouldNotUploadFiles() throws IOException, NoSuchAlgorithmException, InterruptedException {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            Set<DeployItem> files = new HashSet<>();
            DeployItem item1 = new DeployItem("file1.txt", "test".getBytes());
            files.add(item1);

            // act
            service.uploadFiles("1.0", files, null);

            // assert
            mockedConnectionUtilsUtils.verify(() ->
                    ConnectionUtils.uploadFile(eq(config), eq("accessToken"), anyString(), anyString(), any()), never());
        }
    }

    @Test
    void shouldUploadFiles() throws IOException, NoSuchAlgorithmException, InterruptedException {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            config.setDisableAsync(true);
            DeployItem item1 = new DeployItem("file1.txt", "test".getBytes());
            DeployItem item2 = new DeployItem("file2.txt", "test2".getBytes());
            Set<DeployItem> files = new HashSet<>();
            files.add(item1);
            files.add(item2);

            byte[] fileContent = FileUtils.compressAndReadFile(item2.getContent());
            String checkSum = FileUtils.getSHA256Checksum(fileContent);

            List<String> requiredHashes = new ArrayList<>();
            requiredHashes.add(checkSum);

            // act
            service.uploadFiles("1.0", files, requiredHashes);

            // assert
            mockedConnectionUtilsUtils.verify(() ->
                    ConnectionUtils.uploadFile(eq(config), eq("accessToken"), anyString(), anyString(), any()));
        }
    }

    @Test
    void shouldUploadFilesAsync() throws IOException, NoSuchAlgorithmException, InterruptedException {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            DeployItem item1 = new DeployItem("file1.txt", "test".getBytes());
            DeployItem item2 = new DeployItem("file2.txt", "test2".getBytes());
            Set<DeployItem> files = new HashSet<>();
            files.add(item1);
            files.add(item2);

            byte[] fileContent = FileUtils.compressAndReadFile(item2.getContent());
            String checkSum = FileUtils.getSHA256Checksum(fileContent);

            List<String> requiredHashes = new ArrayList<>();
            requiredHashes.add(checkSum);

            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.uploadFile(eq(config), anyString(), anyString(), anyString(), any()))
                    .thenThrow(new IOException("Test"));

            // act
            service.uploadFiles("1.0", files, requiredHashes);

            // assert
            mockedConnectionUtilsUtils.verify(() ->
                    ConnectionUtils.uploadFile(eq(config), eq("accessToken"), anyString(), anyString(), any()), never());
        }
    }

    @ParameterizedTest
    @MethodSource("failureTestData")
    void shouldNotUploadFileAsync(Exception e) {
        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
            // arrange
            CountDownLatch latch = new CountDownLatch(1);
            mockedStatic.when(() -> FileUtils.getSHA256Checksum(any())).thenThrow(e);
            FileUploadItem item = new FileUploadItem("test".getBytes(), "test");

            // act
            service.uploadFileAsync(item, "1.0", latch);

            // assert
            mockedStatic.verify(() -> FileUtils.getSHA256Checksum(any()));
        }
    }

    private static Object[] failureTestData() {
        return new Object[] {
                new NoSuchAlgorithmException("Test"),
                new IOException("Test")
        };
    }

    private static Object[][] uploadFileData() {
        return new Object[][] {
                { "", "/1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014" },
                { "upload-url", "upload-url/1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014" }
        };
    }
}
