# firebase-hosting-api-java
A simple Java client library for Firebase Hosting REST API (https://firebase.google.com/docs/reference/hosting/rest).

**IMPORTANT: This library is compatible only with V1BETA1 services!!**

## Current status

| Build                                                        | Code coverage                                                | Code quality                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [![CodeQL](https://github.com/peter-szrnka/firebase-hosting-api-java/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/peter-szrnka/firebase-hosting-api-java/actions/workflows/github-code-scanning/codeql) | ![Code coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=coverage) | [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_firebase-hosting-api-java&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_firebase-hosting-api-java) |

# Used technologies

- Java 8 (17 will be the default from version 0.9)
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

Add the following dependency to your Maven pom.xml:

```xml
<dependency>
	<groupId>io.github.peter-szrnka</groupId>
	<artifactId>firebase-hosting-api-java</artifactId>
	<version>0.9</version>
</dependency>
```

**IMPORTANT: In case version < 0.9, please use "io.github.szrnka-peter" as groupId!**

## Object serializers

You have to create your own Serializer by implementing the io.github.szrnkapeter.firebase.hosting.serializer.Serializer interface. For further details, please check https://github.com/peter-szrnka/firebase-hosting-api-java/wiki/Serializers

## Input parameters

| Name                        | Type                                                         | Mandatory? | Description                                                  |
| --------------------------- | ------------------------------------------------------------ | ---------- | ------------------------------------------------------------ |
| siteId                      | String                                                       | Yes        | Unique identifier of the Firebase Hosting website            |
| serializer                  | io.github.szrnkapeter.firebase.hosting.serializer.Serializer | Yes        | JSON serializer class                                        |
| service account file stream | InputStream                                                  | Yes        | Firebase service account JSON file                           |
| default connection timeout  | int (default 30000)                                          | No         | Connection timeout                                           |
| default read timeout        | int (default 30000)                                          | No         | Read timeout                                                 |
| httpResponseCallback        | io.github.szrnkapeter.firebase.hosting.callback.HttpResponseCallback | No         | Callback function that returns with the value of the HTTP response for the give service |
| serviceResponseCallback     | io.github.szrnkapeter.firebase.hosting.callback.ServiceResponseCallback | No         | Callback function that returns with the value of the given service |
| disableAsync                | boolean                                                      | No         | Async file upload can be turned off with this flag           |

**IMPORTANT: If you do not set the:**
- **httpResponseCallback**
- **serviceResponseCallback**

**parameters then you will not see any response codes or messages!**

## Firebase configuration
<u>Detailed guide can be found [here](https://github.com/peter-szrnka/firebase-hosting-api-java/wiki/Firebase-project-setup)</u>

# Usage

**IMPORTANT:** Library does not catch all exceptions. The purpose of this approach is to give the control to your application instead of hiding and wrapping it.

## Samples

You can find samples under "examples" folder!

## New deployment

A new deployment can be started by calling **client.createDeploy()**. The **cleanDeploy** is an important parameter that determines the new deployment should remove the existing files or not.

```java
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
	.withServiceAccountFileStream(new FileInputStream("service-account-iam.json"))
	.withDefaultConnectionTimeout(90000).withDefaultReadTimeout(90000)
    .withSerializer(new GsonSerializer())
	.withHttpResponseCallback(new HttpResponseCallback() {
						
		@Override
		public void getResponseInfo(String function, int code, String responseMessage) {
			System.out.println(function + " / " + code + " / " + responseMessage);
		}
	})
	.withServiceResponseCallback(new ServiceResponseCallback() {
						
		@Override
		public void getResponse(String function, Object response) {
			System.out.println(function + " / " + response);
		}
	})
	.withSiteId("my-site-name") //
	.build();
	
FirebaseHostingApiClient client = FirebaseHostingApiClient.newClient(config);
	
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
	.withServiceAccountFileStream(new FileInputStream("service-account-iam.json"))
	.withSerializer(new GsonSerializer())
	.withSiteId("my-site-name")
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
	.withServiceAccountFileStream(new FileInputStream("service-account-iam.json"))
	.withSiteId("my-site-name")
	.withSerializer(new GsonSerializer())
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

## Adding response callbacks

If you need the HTTP sub service responses of createDeploy() method, you can add callbacks, which'll returns with the HTTP return codes, messages, and service response objects. In the example below, you'll get all HTTP responses, and service response objects.

```java
...
FirebaseHostingApiConfig config = FirebaseHostingApiConfigBuilder.builder()
...
	.withHttpResponseCallback(new HttpResponseCallback() {
		@Override
		public void getResponseInfo(String function, int code, String responseMessage) {
			System.out.println(function + " / " + code + " / " + responseMessage);
		}
	})
	...
	.withServiceResponseCallback(new ServiceResponseCallback() {
						
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

# Donate
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7YEPKTQRNK5YA)
