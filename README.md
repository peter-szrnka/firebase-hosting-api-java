# firebase-hosting-api-java
A simple Java library for Firebase Hosting REST API (https://firebase.google.com/docs/reference/hosting/rest).

**IMPORTANT: This library is compatible only with V1BETA1 services!!**

# Used technologies

- Java 8
- Maven
- Jackson mapper
- Org.json
- Gson

# Implemented services

| resource name                               | Resource name                                                |
| ------------------------------------------- | ------------------------------------------------------------ |
| v1beta1.sites.releases.list                 | Lists all releases                                           |
| v1beta1.sites.versions.files.list           | Lists all files relating to the given version                |
| v1beta1.sites.versions.create               | Creates a new deployment version                             |
| v1beta1.sites.versions.populateFiles        | Sets the files should be downloaded with the upload function |
| v1beta1.sites.versions.uploadRequiredHashes | Uploads the required files populated by populateFiles        |
| v1beta1.sites.versions.patch                | Finalizes a given deploymentversion                          |
| v1beta1.sites.releases.create               | Creates a new release                                        |
| v1beta1.sites.versions.delete               | Deletes a deployment version                                 |

# Configuration

## Maven

```xml
<dependency>
	<groupId>io.github.szrnka-peter</groupId>
	<artifactId>firebase-hosting-api-java</artifactId>
	<version>0.4</version>
</dependency>
```

## Object serializers

- Jackson (default)
- Moshi
- Gson

# Usage

## New deployment

A new deployment can be started by calling **client.createDeploy()**. The **cleanDeploy** is an important parameter that determines the new deployment should remove the existing files or not.

	FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
					.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
	.withDefaultConnectionTimeout(90000).withDefaultReadTimeout(90000).withSerializer(SerializerType.JACKSON)
					.withHttpResponseListener(new HttpResponseListener() {
						
						@Override
						public void getResponseInfo(String function, int code, String responseMessage) {
							System.out.println(function + " / " + code + " / " + responseMessage);
						}
					})
					.withServiceResponseListener(new ServiceResponseListener() {
						
						@Override
						public void getResponse(String function, Object response) {
							System.out.println(function + " / " + response);
						}
					})
					.withSiteName("budapest-parkolas") //
					.build();
	
			FirebaseHostingApiClient client = new FirebaseHostingApiClient(config);
	
			// COMPLETE, WORKING DEPLOYMENT
			DeployRequest request = new DeployRequest();
			request.setCleanDeploy(false);
			DeployItem di1 = new DeployItem();
			di1.setName("test0.txt");
			di1.setContent(Files.readAllBytes(new File("test0.txt").toPath()));
	
			Set<DeployItem> fileList = new HashSet<>();
			fileList.add(di1);
			request.setFiles(fileList);
			client.createDeploy(request);
## Get Releases

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.build();

		FirebaseHostingApiClient client = new FirebaseHostingApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```

## Custom serializer

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.withSerializer(SerializerType.GSON)
    			.build();

		FirebaseHostingApiClient client = new FirebaseHostingApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```

## Custom timeouts

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.withDefaultConnectionTimeout(90000)
				.withDefaultReadTimeout(90000)
    			.build();

		FirebaseHostingApiClient client = new FirebaseHostingApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```

## Adding response listeners

	...
	FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
	...
					.withHttpResponseListener(new HttpResponseListener() {
						@Override
						public void getResponseInfo(String function, int code, String responseMessage) {
							System.out.println(function + " / " + code + " / " + responseMessage);
						}
					})
	...
					.withServiceResponseListener(new ServiceResponseListener() {
						
						@Override
						public void getResponse(String function, Object response) {
							System.out.println(function + " / " + response);
						}
					})
	...