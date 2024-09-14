package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class TestConfigGenerator {

    private static final String SEPARATOR = " / ";

    public static FirebaseHostingApiConfig getFirebaseRestApiConfig() throws Exception {
        return FirebaseHostingApiConfigBuilder.builder()
                .withServiceAccountFileStream(Files.newInputStream(Paths.get("src/test/resources/test.json")))
                .withSiteId("test")
                .withSerializer(new GsonSerializer())
                .withDefaultConnectionTimeout(60000).withDefaultReadTimeout(60000)
                .withHttpResponseCallback((function, code, responseMessage) -> System.out.println(function + SEPARATOR + code + SEPARATOR + responseMessage))
                .withServiceResponseCallback((function, response) -> System.out.println(function + SEPARATOR + response))
                .build();
    }
}
