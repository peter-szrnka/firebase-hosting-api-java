package io.github.szrnkapeter.firebase.hosting.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.listener.ServiceResponseListener;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
class ReleaseServiceImplTest {

    private static final String VERSION_NAME = "version1";

    private FirebaseHostingApiConfig config;
    private ReleaseServiceImpl service;

    @BeforeEach
    @SneakyThrows
    void setup() {
        config = mock(FirebaseHostingApiConfig.class);
        service = new ReleaseServiceImpl(config, "accessToken");

        when(config.getSiteId()).thenReturn("test");
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldCreateRelease(boolean addResponseListener) {
        ServiceResponseListener mockServiceResponseListener = mock(ServiceResponseListener.class);
        if (addResponseListener) {
            doNothing().when(mockServiceResponseListener).getResponse(anyString(), any());
            when(config.getServiceResponseListener()).thenReturn(mockServiceResponseListener);
        }
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            Release mockResponse = new Release();
            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                    ArgumentMatchers.isNull(), ArgumentMatchers.anyString())).thenReturn(mockResponse);

            // act
            Release response = service.createRelease(VERSION_NAME);

            // assert
            assertNotNull(response);
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openSimpleHTTPPostConnection(any(FirebaseHostingApiConfig.class),
                    any(Class.class), ArgumentMatchers.anyString(), urlCaptor.capture(),
                    ArgumentMatchers.isNull(), ArgumentMatchers.anyString()));

            assertEquals("sites/test/releases?versionName=version1", urlCaptor.getValue());
        }

        verify(config, times(addResponseListener ? 2 : 1)).getServiceResponseListener();
        if (addResponseListener) {
            ArgumentCaptor<String> functionCaptor = ArgumentCaptor.forClass(String.class);
            verify(config.getServiceResponseListener()).getResponse(functionCaptor.capture(), any());
            assertEquals("createRelease", functionCaptor.getValue());
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldGetReleases(boolean addResponseListener) {
        ServiceResponseListener mockServiceResponseListener = mock(ServiceResponseListener.class);
        if (addResponseListener) {
            when(config.getServiceResponseListener()).thenReturn(mockServiceResponseListener);
        }
        try (MockedStatic<ConnectionUtils> mockedConnectionUtilsUtils = mockStatic(ConnectionUtils.class)) {
            // arrange
            ObjectMapper objectMapper = new ObjectMapper();
            GetReleasesResponse mockGetReleasesResponse = objectMapper.readValue(
                    new File("src/test/resources/sample-response-get-releases1.json"), GetReleasesResponse.class);

            mockedConnectionUtilsUtils.when(() -> ConnectionUtils.openHTTPGetConnection(any(FirebaseHostingApiConfig.class),
                            any(Class.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenReturn(mockGetReleasesResponse);

            // act
            GetReleasesResponse response = service.getReleases();

            // assert
            assertNotNull(response);
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            mockedConnectionUtilsUtils.verify(() -> ConnectionUtils.openHTTPGetConnection(any(FirebaseHostingApiConfig.class),
                    any(Class.class), ArgumentMatchers.anyString(), urlCaptor.capture()));

            assertEquals("sites/test/releases", urlCaptor.getValue());
        }

        if (addResponseListener) {
            ArgumentCaptor<String> functionCaptor = ArgumentCaptor.forClass(String.class);
            verify(config.getServiceResponseListener()).getResponse(functionCaptor.capture(), any());
            assertEquals("getReleases", functionCaptor.getValue());
        }
    }
}
