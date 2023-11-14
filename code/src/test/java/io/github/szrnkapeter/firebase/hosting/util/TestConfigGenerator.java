package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.callback.ServiceResponseCallback;
import io.github.szrnkapeter.firebase.hosting.serializer.GsonSerializer;

import java.io.FileInputStream;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class TestConfigGenerator {

    private static final String SEPARATOR = " / ";

    public static FirebaseHostingApiConfig getFirebaseRestApiConfig() throws Exception {
        return FirebaseHostingApiConfigBuilder.builder()
                .withServiceAccountFileStream(new FileInputStream("src/test/resources/test.json"))
                .withSiteId("test")
                .withSerializer(new GsonSerializer())
                .withDefaultConnectionTimeout(60000).withDefaultReadTimeout(60000)
                .withHttpResponseCallback((function, code, responseMessage) -> System.out.println(function + SEPARATOR + code + SEPARATOR + responseMessage))
                .withServiceResponseCallback(new ServiceResponseCallback() {

                    @Override
                    public <T> void getResponse(String function, T response) {
                        System.out.println(function + SEPARATOR + response);
                    }
                })
                .build();
    }
}
