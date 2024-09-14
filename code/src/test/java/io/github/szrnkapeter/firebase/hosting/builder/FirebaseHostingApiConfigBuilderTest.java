package io.github.szrnkapeter.firebase.hosting.builder;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FirebaseHostingApiConfigBuilderTest {

    @Test
    void test() throws IOException {
        // act
        FirebaseHostingApiConfig apiConfigResponse = FirebaseHostingApiConfigBuilder
                .builder()
                .withSerializer(new GsonSerializer())
                .withSiteId("site")
                .withDefaultReadTimeout(30000)
                .withDefaultConnectionTimeout(30000)
                .withServiceAccountFileStream(Files.newInputStream(Paths.get("src/test/resources/test.json")))
                .withHttpResponseCallback((function, code, responseMessage) -> {
                    // Do nothing
                })
                .withServiceResponseCallback((function, response) -> {
                        // Do nothing
                    })
                .withDisableAsync(true)
                .withThreadPoolSize(1)
                .build();

        // assert
        assertNotNull(apiConfigResponse);
    }
}
