package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.Version;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.Constants;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
class VersionServiceImplTest {

    private FirebaseHostingApiConfig config;
    private VersionServiceImpl service;

    @BeforeEach
    @SneakyThrows
    void setup() {
        config = mock(FirebaseHostingApiConfig.class);
        service = new VersionServiceImpl(config, "accessToken");

        when(config.getSiteId()).thenReturn("test");
    }

    @Test
    @SneakyThrows
    void shouldCreateVersion() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            String randomName = UUID.randomUUID().toString();
            Version mockResponse = new Version();
            mockResponse.setName(randomName);
            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    any(Class.class), anyString(), anyString(),
                    anyString(), anyString())).thenReturn(mockResponse);

            // act
            Version response = service.createVersion();

            // assert
            assertNotNull(response);
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    any(Class.class), anyString(), anyString(),
                    anyString(), anyString()));
        }
    }

    @Test
    @SneakyThrows
    void shouldDeleteVersion() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // act
            service.deleteVersion("1.0");

            // assert
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPConnection(eq("DELETE"),
                    any(FirebaseHostingApiConfig.class), isNull(), eq("accessToken"), anyString(), isNull(), anyString()));
        }
    }

    @Test
    @SneakyThrows
    void shouldNotDeletePreviousVersions() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            DeployRequest request = new DeployRequest();
            request.setDeletePreviousVersions(false);
            List<Release> releaseList = new ArrayList<>();

            // act
            service.deletePreviousVersions(request, releaseList);

            // assert
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPConnection(eq("DELETE"),
                    any(FirebaseHostingApiConfig.class), isNull(), eq("accessToken"), anyString(), isNull(), anyString()), never());
        }
    }

    @Test
    @SneakyThrows
    void shouldDeletePreviousVersions() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            DeployRequest request = new DeployRequest();
            request.setDeletePreviousVersions(true);
            List<Release> releaseList = new ArrayList<>();
            Release release1 = new Release();
            Version version = new Version();
            version.setName("version-name");
            version.setStatus(Constants.FINALIZED);
            release1.setVersion(version);
            Release release2  = new Release();
            release2.setVersion(version);
            Release release3  = new Release();

            Version version2 = new Version();
            version2.setName("version-name");
            version2.setStatus("TODO");
            release3.setVersion(version2);

            releaseList.add(release1);
            releaseList.add(release2);
            releaseList.add(release3);

            // act
            service.deletePreviousVersions(request, releaseList);

            // assert
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPConnection(eq("DELETE"),
                    any(FirebaseHostingApiConfig.class), isNull(), eq("accessToken"), anyString(), isNull(), eq("deleteVersion")));
        }
    }

    @Test
    @SneakyThrows
    void shouldFinalizeVersions() {
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            String randomName = UUID.randomUUID().toString();
            Version mockResponse = new Version();
            mockResponse.setName(randomName);
            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPConnection(eq("PATCH"),
                            any(FirebaseHostingApiConfig.class), any(Class.class),
                            eq("accessToken"), eq("sites/test/versions/1.0?update_mask=status"),
                            eq("{ \"status\": \"FINALIZED\" }"), eq("finalizeVersion")))
                    .thenReturn(mockResponse);

            // act
            service.finalizeVersion("1.0");

            // assert
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPConnection(eq("PATCH"),
                    any(FirebaseHostingApiConfig.class), any(Class.class),
                    eq("accessToken"), eq("sites/test/versions/1.0?update_mask=status"),
                    eq("{ \"status\": \"FINALIZED\" }"), eq("finalizeVersion")));
        }
    }
}
