# firebase-hosting-api-java
A simple Java client library for Firebase Hosting REST API (https://firebase.google.com/docs/reference/hosting/rest).

**IMPORTANT: This library is compatible only with V1BETA1 services!!**

## Current status

| Build                                                        | Code coverage                                                | Code quality                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [![CodeQL](https://github.com/peter-szrnka/firebase-hosting-api-java/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/peter-szrnka/firebase-hosting-api-java/actions/workflows/codeql-analysis.yml) | ![Code coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=coverage) | [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) |

# Used technologies

- Java 8
- Maven

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
	<groupId>io.github.peter-szrnka</groupId>
	<artifactId>firebase-hosting-api-java</artifactId>
	<version>0.6</version>
</dependency>
```

## Object serializers

Built-in object serializers are deprecated from version 0.5:

- Jackson (default)
- Moshi
- Gson

From version 0.6, they're totally removed from the package. You have to create your own Serializer by implementing the io.github.szrnkapeter.firebase.hosting.serializer.Serializer interface. For further details, please check https://github.com/peter-szrnka/firebase-hosting-api-java/wiki/Serializers

# Usage

## New deployment

A new deployment can be started by calling **client.createDeploy()**. The **cleanDeploy** is an important parameter that determines the new deployment should remove the existing files or not.

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
	.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
	.withDefaultConnectionTimeout(90000).withDefaultReadTimeout(90000).withCustomSerializer(new GsonSerializer())
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
	.withSiteName("my-site-name") //
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
```
## Get Releases

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
	.withConfigStream(new FileInputStream("firebase-adminsdk.json"))
	.withCustomSerializer(new GsonSerializer())
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
    	.withCustomSerializer(new MoshiSerializer())
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
	.withCustomSerializer(new GsonSerializer())
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

If you need the HTTP sub service responses of createDeploy() method, you can add listeners, which'll returns with the HTTP return codes, messages, and service response objects. In the example below, you'll get all HTTP responses, and service response objects.

```java
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
```
**Example log**

```
createVersion / 200 / OK
createVersion / Version [name=sites/<site-name>/versions/<version-name>, status=CREATED, config={}, labels=null, createTime=null, createUser=null, finalizeTime=null, finalizeUser=null, fileCount=null, versionBytes=null, deleteUser=null, deleteTime=null, preview=null]
populateFiles / 200 / OK
populateFiles / PopulateFilesResponse [uploadRequiredHashes=null, uploadUrl=https://upload-firebasehosting.googleapis.com/upload/sites/<site-name>/versions/<version-name>/files]
uploadFile / 200 / OK
uploadFile / 200 / OK
uploadFile / 200 / OK
uploadFile / 200 / OK
uploadFile / 200 / OK
finalizeVersion / 200 / OK
finalizeVersion / Version [name=sites/<site-name>/versions/<version-name>, status=FINALIZED, config={}, labels=null, createTime=Mon Dec 28 17:20:18 CET 2020, createUser=User [email=firebase-adminsdk-to1iz@<site-name>.iam.gserviceaccount.com, imageUrl=null], finalizeTime=Mon Dec 28 17:20:29 CET 2020, finalizeUser=User [email=firebase-adminsdk-to1iz@<site-name>.iam.gserviceaccount.com, imageUrl=null], fileCount=null, versionBytes=null, deleteUser=null, deleteTime=null, preview=null]
createRelease / 200 / OK
createRelease / Release [name=sites/<site-name>/releases/<release-id>, type=DEPLOY, releaseTime=Mon Dec 28 17:20:29 CET 2020, releaseUser=User [email=firebase-adminsdk-to1iz@<site-name>.iam.gserviceaccount.com, imageUrl=null], version=Version [name=sites/<site-name>/versions/<version-name>, status=FINALIZED, config={}, labels=null, createTime=Mon Dec 28 17:20:18 CET 2020, createUser=User [email=firebase-adminsdk-to1iz@<site-name>.iam.gserviceaccount.com, imageUrl=null], finalizeTime=Mon Dec 28 17:20:29 CET 2020, finalizeUser=User [email=firebase-adminsdk-to1iz@<site-name>.iam.gserviceaccount.com, imageUrl=null], fileCount=null, versionBytes=null, deleteUser=null, deleteTime=null, preview={}]]
```
