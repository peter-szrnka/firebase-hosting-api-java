package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.github.szrnkapeter.firebase.hosting.util.TestConfigGenerator.getFirebaseRestApiConfig;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
class VersionUtilsTest {

    public static final String VERSION_RESULT = "sites/test/versions/1.0";

    @Test
    void testPrivateConstructor() {
        assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(VersionUtils.class));
    }

    @ParameterizedTest
    @MethodSource("versionIdTestData")
    void shouldGetVersionId(String input, String expectedResponse) {

        // act
        String response = VersionUtils.getVersionId(input);

        // assert
        assertEquals(expectedResponse, response);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("versionNameFailedTestData")
    void shouldFailToGetVersionName(String input) {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();

        // act & assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> VersionUtils.getVersionName(config, input));
        assertEquals("Version field is mandatory!", exception.getMessage());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("versionNameTestData")
    void shouldGetVersionName(String input, String expectedResponse) {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();

        // act
        String response = VersionUtils.getVersionName(config, input);

        // assert
        assertEquals(expectedResponse, response);
    }

    private static Object[][] versionIdTestData() {
        return new Object[][] {
                { null, null },
                { "", null },
                { "/version", "version" },
                { "version", "version" }
        };
    }

    private static Object[] versionNameFailedTestData() {
        return new Object[] { null, "" };
    }

    private static Object[][] versionNameTestData() {
        return new Object[][] {
                { "1.0", VERSION_RESULT},
                {VERSION_RESULT, VERSION_RESULT}
        };
    }
}
