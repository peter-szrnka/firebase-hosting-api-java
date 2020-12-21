# firebase-hosting-api-java
A simple Java library for Firebase Hosting REST API (https://firebase.google.com/docs/reference/hosting/rest). In the future (2021 Q1) I'll plan to implement all services.

# Used technologies

- Java 8
- Maven
- Jackson mapper
- Org.json
- Gson

# Implemented services

| resource name             | Resource name                                 |
| ------------------------- | --------------------------------------------- |
| sites.releases.list       | Lists all releases                            |
| sites.versions.files.list | Lists all files relating to the given version |

# Configuration

## Maven

```java
<dependency>
	<groupId>hu.szrnkapeter.firebase.hosting</groupId>
	<artifactId>firebase-hosting-api-java</artifactId>
	<version>0.1</version>
</dependency>
```

## Object serializers

- Jackson (default)
- Moshi
- Gson

# Usage

## Simple call

```java
FirebaseRestApiConfig config = FirebaseRestApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.build();

		FirebaseRestApiClient client = new FirebaseRestApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```

## Custom serializer

```java
FirebaseRestApiConfig config = FirebaseRestApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.withSerializer(SerializerType.GSON)
    			.build();

		FirebaseRestApiClient client = new FirebaseRestApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```

## Custom timeouts

```java
FirebaseRestApiConfig config = FirebaseRestApiConfigBuilder.builder()
				.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
				.withSiteName("my-site-name")
    			.withDefaultConnectionTimeout(90000)
				.withDefaultReadTimeout(90000)
    			.build();

		FirebaseRestApiClient client = new FirebaseRestApiClient(config);

		// Call getReleases
		GetReleasesResponse response = client.getReleases();
		System.out.println("Response = " + response);
		
		System.out.println("\r\n");
		
		GetVersionFilesResponse files = client.getVersionFiles(response.getReleases().get(0).getVersion().getName());
		System.out.println("Files response = " + files);
```
