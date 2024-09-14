package hu.szrnkapeter.sample;

import io.github.szrnkapeter.firebase.hosting.FirebaseHostingApiClient;
import io.github.szrnkapeter.firebase.hosting.builder.FirebaseHostingApiConfigBuilder;
import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample usage with v0.9-SNAPSHOT
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Start");
            long startTime = System.currentTimeMillis();
            InputStream serviceAccountStream = Main.class.getClassLoader().getResourceAsStream("service-account.json");

            FirebaseHostingApiConfig firebaseRestApiConfig = FirebaseHostingApiConfigBuilder.builder() //
                    .withDefaultConnectionTimeout(90000) //
                    .withDefaultReadTimeout(90000) //
                    .withServiceAccountFileStream(serviceAccountStream) //
                    .withSerializer(new GsonSerializer())
                    // Uncomment these lines if you need the HTTP & service response data !!!!!!!!!!
                    // (Optional) HTTP response listener to show & process HTTP response data
                    .withHttpResponseCallback((function, code, responseMessage) ->
                            System.out.println(Thread.currentThread().getName() + "/" + function + " / " + code + " / " + responseMessage))
                    // (Optional) Service response listener to show & process service response data
                    .withServiceResponseCallback((function, response) ->
                            System.out.println(Thread.currentThread().getName() + "/" + function + " / " + response))
                    .withSiteId(System.getenv("FIREBASE_SITE_ID")) //
                    //.withDisableAsync(true) // This will disable the async mode
                    .build();
            FirebaseHostingApiClient client = FirebaseHostingApiClient.newClient(firebaseRestApiConfig);

            DeployRequest deployRequest = new DeployRequest();
            deployRequest.setCleanDeploy(true);
            deployRequest.setDeletePreviousVersions(true);

            deployRequest.setFiles(createDeployItems());
            client.createDeploy(deployRequest);

            long endTime = System.currentTimeMillis();
            System.out.println("End: " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<DeployItem> createDeployItems() {
        Set<DeployItem> files = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            files.add(new DeployItem("file" + i, ("test" + i).getBytes()));
        }

        return files;
    }
}
