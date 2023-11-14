package io.github.szrnkapeter.firebase.hosting.builder;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.callback.ServiceResponseCallback;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FirebaseHostingApiConfigBuilderTest {

    @Test
    void test() throws FileNotFoundException {
        // act
        FirebaseHostingApiConfig response = FirebaseHostingApiConfigBuilder
                .builder()
                .withSerializer(new GsonSerializer())
                .withSiteId("site")
                .withDefaultReadTimeout(30000)
                .withDefaultConnectionTimeout(30000)
                .withServiceAccountFileStream(new FileInputStream("src/test/resources/test.json"))
                .withHttpResponseCallback((function, code, responseMessage) -> {
                    // Do nothing
                })
                .withServiceResponseCallback(new ServiceResponseCallback() {
                    @Override
                    public <T> void getResponse(String function, T response) {
                        // Do nothing
                    }
                })
                .build();

        // assert
        assertNotNull(response);
    }
}
