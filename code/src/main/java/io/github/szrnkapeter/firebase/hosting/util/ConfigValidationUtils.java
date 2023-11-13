package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * API config validation utility
 *
 * @author Peter Szrnka
 * @since 0.8
 */
public class ConfigValidationUtils {

    private ConfigValidationUtils() {}

    /**
     * Pre-validation is essential to check the input configuration and avoid any unwanted issues.
     *
     * @param firebaseRestApiConfig A {@link FirebaseHostingApiConfig} instance
     */
    public static void preValidateConfig(FirebaseHostingApiConfig firebaseRestApiConfig) {
        if (firebaseRestApiConfig == null) {
            throw new IllegalArgumentException("FirebaseRestApiConfig is mandatory!");
        }

        if (firebaseRestApiConfig.getSiteId() == null || firebaseRestApiConfig.getSiteId().isEmpty()) {
            throw new IllegalArgumentException("Site ID is mandatory!");
        }

        if (firebaseRestApiConfig.getServiceAccountFileStream() == null) {
            throw new IllegalArgumentException("Service account file stream is missing from the configuration!");
        }

        if (firebaseRestApiConfig.getSerializer() == null) {
            throw new IllegalArgumentException("Serializer is missing from the configuration!");
        }
    }
}
