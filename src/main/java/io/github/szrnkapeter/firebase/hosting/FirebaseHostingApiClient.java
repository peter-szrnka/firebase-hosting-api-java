package io.github.szrnkapeter.firebase.hosting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;
import io.github.szrnkapeter.firebase.hosting.model.DeployResponse;
import io.github.szrnkapeter.firebase.hosting.model.FileDetails;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesRequest;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.UploadFileRequest;
import io.github.szrnkapeter.firebase.hosting.model.Version;
import io.github.szrnkapeter.firebase.hosting.serializer.SerializerFactory;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.Constants;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

/**
 * Firebase REST API client for Java
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FirebaseHostingApiClient {

	private static final String FILES = "/files";
	private static final String VERSIONS = "/versions/";
	private static final String SITES = "sites/";

	private FirebaseHostingApiConfig config;
	private String accessToken;

	/**
	 * Default constructor.
	 * 
	 * @param firebaseRestApiConfig A new {@link FirebaseHostingApiConfig}
	 * @throws IOException An unwanted IOException
	 * 
	 * @since 0.2
	 */
	public FirebaseHostingApiClient(FirebaseHostingApiConfig firebaseRestApiConfig) throws IOException {
		if(firebaseRestApiConfig == null) {
			throw new IllegalArgumentException("FirebaseRestApiConfig field is mandatory!");
		}

		config = firebaseRestApiConfig;
		accessToken = GoogleCredentialUtils.getAccessToken(config);
	}

	/**
	 * Creates a complete deployment by calling all required services in the right order:
	 * <ul>
	 * <li>createVersion</li>
	 * <li>populateFiles</li>
	 * <li>uploadFile..</li>
	 * <li>finalizeVersion</li>
	 * <li>createRelease</li>
	 * </ul>
	 * 
	 * If it's a clean deploy, all previous files will be deleted.
	 * 
	 * @param request A {@link DeployRequest} instance.
	 * @return A new {@link DeployResponse} object.
	 * @throws Exception Any exception occured in this service.
	 * 
	 * @since 0.4
	 */
	public DeployResponse createDeploy(DeployRequest request) throws Exception {
		if(!request.isCleanDeploy()) {
			GetReleasesResponse getReleases = getReleases();
			
			if(getReleases == null || getReleases.getReleases() == null || getReleases.getReleases().isEmpty()) {
				return null;
			}
			
			String versionId = getVersionId(getReleases.getReleases().get(0).getVersion().getName());
			GetVersionFilesResponse getVersionFiles = getVersionFiles(versionId);
			
			Set<String> newFileNames = request.getFiles().stream().map(file -> file.getName()).collect(Collectors.toSet());

			for(FileDetails file : getVersionFiles.getFiles()) {
				String fileName = file.getPath().substring(1);

				if(fileName.startsWith("__/") || newFileNames.contains(fileName)) {
					continue;
				}

				byte[] fileContent = FileUtils.getRemoteFile("https://" + config.getSiteName() + ".firebaseapp.com/" + fileName);
				request.getFiles().add(new DeployItem(fileName, fileContent));
			}
		}
		
		// Creation of the new version
		Version newVersion = createVersion();
		String versionId = getVersionId(newVersion.getName());

		// Populate the files
		PopulateFilesRequest populateRequest = new PopulateFilesRequest();
		populateRequest.setFiles(generateFileListAndHash(request.getFiles()));
		populateFiles(populateRequest, versionId);

		// Upload them
		for(DeployItem item : request.getFiles()) {
			byte[] fileContent = FileUtils.compressAndReadFile(item.getContent());
			UploadFileRequest uploadFilesRequest = new UploadFileRequest();
			uploadFilesRequest.setVersion(versionId);
			uploadFilesRequest.setFileContent(fileContent);
			uploadFile(uploadFilesRequest);
		}

		// Finalize the new version
		finalizeVersion(versionId);
		
		// Create the release
		Release newRelease = createRelease(versionId);
		
		// Post delete earlier deployments
		if(request.isDeletePreviousVersions()) {
			GetReleasesResponse response = getReleases();
			AtomicInteger i = new AtomicInteger(0);
			
			for(Release release : response.getReleases()) {
				if(i.get() > 0 && Constants.FINALIZED.equals(release.getVersion().getStatus())) {
					deleteVersion(release.getVersion().getName());
				}
				
				i.incrementAndGet();
			}
		}

		// Create the release
		return new DeployResponse(newRelease);
	}

	private Map<String, String> generateFileListAndHash(Set<DeployItem> files) throws Exception {
		Map<String, String> result = new HashMap<>();

		for(DeployItem file : files) {
			byte[] gzippedContent = FileUtils.compressAndReadFile(file.getContent());
			String checkSum = FileUtils.getSHA256Checksum(gzippedContent);
			result.put("/" + file.getName(), checkSum);
		}
		
		return result;
	}

	/**
	 * Creates a new release.
	 * 
	 * @param version The version ID that we want to release
	 * @return A new {@link Release}
	 * @throws Exception An unwanted Exception
	 * 
	 * @since 0.2
	 */
	public Release createRelease(String version) throws Exception {	
		Release newRelease =  ConnectionUtils.openSimpleHTTPPostConnection(config, Release.class, accessToken,
			SITES + config.getSiteName() + "/releases?versionName=" + getVersionName(version), null, "createRelease");
		
		if(config.getServiceResponseListener() != null) {
			config.getServiceResponseListener().getResponse("createRelease", newRelease);
		}
		
		return newRelease;
	}

	/**
	 * Creates a new version by the given parameters.
	 * 
	 * @return A new {@link Version}
	 * @throws Exception Any unwanted Exception
	 * 
	 * @since 0.2
	 */
	public Version createVersion() throws Exception {
		Version newVersion = ConnectionUtils.openSimpleHTTPPostConnection(config, Version.class, accessToken,
				SITES + config.getSiteName() + "/versions", "{}", "createVersion");
		
		if(config.getServiceResponseListener() != null) {
			config.getServiceResponseListener().getResponse("createVersion", newVersion);
		}
		
		return newVersion;
	}

	/**
	 * Deletes a version by ID. 
	 * 
	 * @param version The version ID that we want to delete.
	 * @throws Exception Any unwanted Exception
	 * 
	 * @since 0.2
	 */
	public void deleteVersion(String version) throws Exception {
		ConnectionUtils.openSimpleHTTPConnection("DELETE", config, null, accessToken, getVersionName(version), null, "deleteVersion");
	}

	/**
	 * Updates the status of the given version to finalized.
	 * 
	 * @param version The version ID that we want to finalize.
	 * @return A new {@link Version}
	 * @throws Exception Any unwanted Exception
	 * 
	 * @since 0.2
	 */
	public Version finalizeVersion(String version) throws Exception {
		Version newVersion = ConnectionUtils.openSimpleHTTPConnection("PATCH", config, Version.class, accessToken,
				SITES + config.getSiteName() + VERSIONS + version + "?update_mask=status", "{ \"status\": \"FINALIZED\" }", "finalizeVersion");

		if(config.getServiceResponseListener() != null) {
			config.getServiceResponseListener().getResponse("finalizeVersion", newVersion);
		}

		return newVersion;
	}

	/**
	 * Returns with the list of releases.
	 * 
	 * @throws MalformedURLException The unexpected MalformedURLException
	 * @throws IOException The unexpected IOException
	 * @return A new {@link GetReleasesResponse} instance
	 * 
	 * @since 0.2
	 */
	public GetReleasesResponse getReleases() throws Exception {
		return ConnectionUtils.openHTTPGetConnection(config, GetReleasesResponse.class, accessToken,
				SITES + config.getSiteName() + "/releases");
	}

	/**
	 * Returns with all files of a given version.
	 * 
	 * @param version Firebase version name
	 * @return A {@link GetVersionFilesResponse} response
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public GetVersionFilesResponse getVersionFiles(String version) throws Exception {
		return ConnectionUtils.openHTTPGetConnection(config, GetVersionFilesResponse.class, accessToken, getVersionName(version) + FILES);
	}

	/**
	 * Calls the populateFiles endpoint.
	 * 
	 * @param request A {@link PopulateFilesRequest} request object
	 * @param version Firebase version name
	 * @return A {@link PopulateFilesResponse} response.
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws Exception {
		String data = SerializerFactory.getSerializer(config).toJson(PopulateFilesRequest.class, request);
		PopulateFilesResponse response = ConnectionUtils.openSimpleHTTPPostConnection(config, PopulateFilesResponse.class, accessToken, SITES + config.getSiteName() + VERSIONS + version + ":populateFiles", data, "populateFiles");

		if(config.getServiceResponseListener() != null) {
			config.getServiceResponseListener().getResponse("populateFiles", response);
		}

		return response;
	}
	
	private String getVersionId(String version) {
		if(version == null || version.isEmpty()) {
			return null;
		}

		return version.substring(version.lastIndexOf("/") + 1);
	}
	
	private String getVersionName(String version) {
		if(version == null || version.isEmpty()) {
			throw new IllegalArgumentException("Version field is mandatory!");
		}

		String versionName = SITES + config.getSiteName() + VERSIONS + version;

		Pattern p = Pattern.compile(SITES + config.getSiteName() + VERSIONS + ".*");
		Matcher m = p.matcher(version);

		if (m.matches()) {
			versionName = version;
		}
		
		return versionName;
	}
	
	/**
	 * Uploads a file.
	 * 
	 * @param request A {@link UploadFileRequest} instance.
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public void uploadFile(UploadFileRequest request) throws Exception {
		String calculatedHash = FileUtils.getSHA256Checksum(request.getFileContent());
		String url = Constants.UPLOAD_FIREBASE_API_URL + "upload/" + SITES + config.getSiteName() + VERSIONS + request.getVersion() + FILES + "/" + calculatedHash;
		
		if(request.getUploadUrl() != null) {
			url = request.getUploadUrl() + "/" + calculatedHash;
		}

		ConnectionUtils.uploadFile(config, accessToken, url, request.getFileContent());
	}
}