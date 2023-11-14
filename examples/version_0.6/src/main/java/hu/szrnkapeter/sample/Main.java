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
 * Sample usage with v0.6
 */
public class Main {
    
    public static void main(String[] args) {
		try {
        System.out.println("Start");
		InputStream apiSdkStream = Main.class.getClassLoader().getResourceAsStream("service-account.json");

			FirebaseHostingApiConfig firebaseRestApiConfig = FirebaseHostingApiConfigBuilder.builder() //
				.withDefaultConnectionTimeout(90000) //
				.withDefaultReadTimeout(90000) //
				.withConfigStream(apiSdkStream) //
				// This will be renamed to withSerializer() in v0.7
				.withCustomSerializer(new GsonSerializer())
				// Uncomment these lines if you need the HTTP & service response data !!!!!!!!!!
				// (Optional) HTTP response listener to show & process HTTP response data
				.withHttpResponseListener((function, code, responseMessage) -> System.out.println(function + " / " + code + " / " + responseMessage))
				// (Optional) Service response listener to show & process service response data
				/*.withServiceResponseListener(new ServiceResponseListener() {
					@Override
					public <T> void getResponse(String function, T response) {
						System.out.println(function + " / " + response);
					}
				})*/
				// You have to define a site ID here -> will be renamed to withSiteId() in v0.7
				.withSiteName("fir-hosting-api-java-test")
					.build();
			FirebaseHostingApiClient client = new FirebaseHostingApiClient(firebaseRestApiConfig);

			DeployRequest deployRequest = new DeployRequest();
			deployRequest.setCleanDeploy(true);
			deployRequest.setDeletePreviousVersions(true);
			Set<DeployItem> files = new HashSet<>();
			files.add(new DeployItem("file1", "test1".getBytes()));
			deployRequest.setFiles(files);
			client.createDeploy(deployRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
