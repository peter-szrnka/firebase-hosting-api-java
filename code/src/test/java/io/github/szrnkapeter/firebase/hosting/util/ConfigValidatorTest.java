package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static io.github.szrnkapeter.firebase.hosting.util.TestConfigGenerator.getFirebaseRestApiConfig;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
class ConfigValidatorTest {

    @Test
    void testPrivateConstructor() {
        assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(ConfigValidatorTest.class));
    }

    @Test
    void shouldThrowErrorWhenConfigIsMissing() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.preValidateConfig(null));
        assertEquals("FirebaseRestApiConfig is mandatory!", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldThrowErrorWhenSiteIdIsMissing() {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();
        config.setSiteId(null);

        // act & assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.preValidateConfig(config));
        assertEquals("Site ID is mandatory!", exception.getMessage());
    }


    @Test
    @SneakyThrows
    void shouldThrowErrorWhenServiceAccountFileStreamIsMissing() {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();
        config.setServiceAccountFileStream(null);

        // act & assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.preValidateConfig(config));
        assertEquals("Service account file stream is missing from the configuration!", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldThrowErrorWhenSerializerIsMissing() {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();
        config.setSerializer(null);

        // act & assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ConfigValidator.preValidateConfig(config));
        assertEquals("Serializer is missing from the configuration!", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void shouldNotThrowError() {
        // arrange
        FirebaseHostingApiConfig config = getFirebaseRestApiConfig();

        // act & assert
        assertDoesNotThrow(() -> ConfigValidator.preValidateConfig(config));
    }
}