# firebase-hosting-api-java
A simple Java library for Firebase Hosting REST API (https://firebase.google.com/docs/reference/hosting/rest). In the future (2021 Q1) I'll plan to implement all services.

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
	<version>0.3</version>
</dependency>
```

## Object serializers

- Jackson (default)
- Moshi
- Gson

# Usage

## Simple call

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
