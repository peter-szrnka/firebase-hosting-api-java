package io.github.szrnkapeter.firebase.hosting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

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

	private final FirebaseHostingApiConfig config;
	private final String accessToken;

	/**
	 * Default constructor.
	 * 
	 * @param firebaseRestApiConfig A new {@link FirebaseHostingApiConfig}
	 * 
	 * @since 0.2
	 */
	public FirebaseHostingApiClient(FirebaseHostingApiConfig firebaseRestApiConfig) {
		preValidateConfig(firebaseRestApiConfig);

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
	 * @throws IOException Any exception occured in this service.
	 * @throws NoSuchAlgorithmException Any exception occured in this service.
	 * 
	 * @since 0.4
	 */
	public DeployResponse createDeploy(DeployRequest request) throws IOException, NoSuchAlgorithmException {
		if(!request.isCleanDeploy()) {
			GetReleasesResponse getReleases = getReleases();
			
			if(getReleases == null || getReleases.getReleases() == null || getReleases.getReleases().isEmpty()) {
				return null;
			}
			
			String versionId = getVersionId(getReleases.getReleases().get(0).getVersion().getName());
			GetVersionFilesResponse getVersionFiles = getVersionFiles(versionId);
			
			Set<String> newFileNames = request.getFiles().stream().map(DeployItem::getName).collect(Collectors.toSet());

			for(FileDetails file : getVersionFiles.getFiles()) {
				String fileName = file.getPath().substring(1);
				if(fileName.startsWith("__/") || newFileNames.contains(fileName)) {
					continue;
				}

				byte[] fileContent = FileUtils.getRemoteFile("https://" + config.getSiteId() + ".firebaseapp.com/" + fileName);
				request.getFiles().add(new DeployItem(fileName, fileContent));
			}
		}

		// Creation of the new version
		Version newVersion = createVersion();
		String versionId = getVersionId(newVersion.getName());

		// Populate the files
		PopulateFilesRequest populateRequest = new PopulateFilesRequest();
		populateRequest.setFiles(generateFileListAndHash(request.getFiles()));
		PopulateFilesResponse populateFilesResponse = populateFiles(populateRequest, versionId);

		// Upload them
		uploadFiles(versionId, request, populateFilesResponse);

		// Finalize the new version
		finalizeVersion(versionId);
		
		// Create the release
		Release newRelease = createRelease(versionId);
		
		// Post delete earlier deployments
		deletePreviousVersion(request);

		// Create the release
		return new DeployResponse(newRelease);
	}

	/**
	 * Creates a new release. Called endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.releases/create">sites.releases/create</a>
	 *
	 * @param version The version ID that we want to release
	 * @return A new {@link Release}
	 * @throws IOException An unwanted Exception
	 *
	 * @since 0.2
	 */
	public Release createRelease(String version) throws IOException {	
		Release newRelease =  ConnectionUtils.openSimpleHTTPPostConnection(config, Release.class, accessToken,
			SITES + config.getSiteId() + "/releases?versionName=" + getVersionName(version), null, "createRelease");

		responseCallback("createRelease", newRelease);
		return newRelease;
	}

	/**
	 * Creates a new version by the given parameters. Called endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/create">sites.versions/create</a>
	 *
	 * @return A new {@link Version}
	 * @throws IOException Any unwanted Exception
	 *
	 * @since 0.2
	 */
	public Version createVersion() throws IOException {
		Version newVersion = ConnectionUtils.openSimpleHTTPPostConnection(config, Version.class, accessToken,
				SITES + config.getSiteId() + "/versions", "{}", "createVersion");

		responseCallback("createVersion", newVersion);
		return newVersion;
	}

	/**
	 * Deletes a version by ID. Called endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/delete">sites.versions/delete</a>
	 *
	 * @param version The version ID that we want to delete.
	 * @throws IOException Any unwanted Exception
	 *
	 * @since 0.2
	 */
	public void deleteVersion(String version) throws IOException {
		ConnectionUtils.openSimpleHTTPConnection("DELETE", config, null, accessToken, getVersionName(version), null, "deleteVersion");
	}

	/**
	 * Updates the status of the given version to finalized. Called endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/patch">sites.versions/patch</a>
	 *
	 * @param version The version ID that we want to finalize.
	 * @return A new {@link Version}
	 * @throws IOException Any unwanted Exception
	 *
	 * @since 0.2
	 */
	public Version finalizeVersion(String version) throws IOException {
		Version newVersion = ConnectionUtils.openSimpleHTTPConnection("PATCH", config, Version.class, accessToken,
				SITES + config.getSiteId() + VERSIONS + version + "?update_mask=status", "{ \"status\": \"FINALIZED\" }", "finalizeVersion");

		responseCallback("finalizeVersion", newVersion);
		return newVersion;
	}

	/**
	 * Returns with the list of releases. It calls the list releases endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.releases/list">sites.releases/list</a>
	 *
	 * @throws MalformedURLException The unexpected MalformedURLException
	 * @throws IOException The unexpected IOException
	 * @return A new {@link GetReleasesResponse} instance
	 *
	 * @since 0.2
	 */
	public GetReleasesResponse getReleases() throws IOException {
		GetReleasesResponse response = ConnectionUtils.openHTTPGetConnection(config, GetReleasesResponse.class, accessToken,
				SITES + config.getSiteId() + "/releases");

		responseCallback("getReleases", response);
		return response;
	}

	/**
	 * Returns with all files of a given version. Called endpoint:
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions.files/list">sites.versions.files/list</a>
	 *
	 * @param version Firebase version name
	 * @return A {@link GetVersionFilesResponse} response
	 * @throws IOException The unexpected exception
	 *
	 * @since 0.2
	 */
	public GetVersionFilesResponse getVersionFiles(String version) throws IOException {
		GetVersionFilesResponse response = ConnectionUtils.openHTTPGetConnection(config, GetVersionFilesResponse.class, accessToken, getVersionName(version) + FILES);

		responseCallback("getVersionFiles", response);
		return response;
	}

	/**
	 * Calls the populateFiles endpoint.
	 * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/populateFiles">sites.versions/populateFiles</a>
	 *
	 * @param request A {@link PopulateFilesRequest} request object
	 * @param version Firebase version name
	 * @return A {@link PopulateFilesResponse} response.
	 * @throws IOException The unexpected exception
	 *
	 * @since 0.2
	 */
	public PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws IOException {
		String data = config.getSerializer().toJson(PopulateFilesRequest.class, request);
		PopulateFilesResponse response = ConnectionUtils.openSimpleHTTPPostConnection(config, PopulateFilesResponse.class, accessToken, SITES + config.getSiteId() + VERSIONS + version + ":populateFiles", data, "populateFiles");

		responseCallback("populateFiles", response);
		return response;
	}

	
	/**
	 * Uploads a file.
	 * 
	 * @param request A {@link UploadFileRequest} instance.
	 * @throws NoSuchAlgorithmException Thrown by getSHA256Checksum
	 * @throws IOException The unexpected exception
	 * 
	 * @since 0.2
	 */
	public void uploadFile(UploadFileRequest request) throws NoSuchAlgorithmException, IOException {
		String calculatedHash = FileUtils.getSHA256Checksum(request.getFileContent());
		String url = Constants.UPLOAD_FIREBASE_API_URL + "upload/" + SITES + config.getSiteId() + VERSIONS + request.getVersion() + FILES + "/" + calculatedHash;
		
		if(request.getUploadUrl() != null) {
			url = request.getUploadUrl() + "/" + calculatedHash;
		}

		ConnectionUtils.uploadFile(config, accessToken, request.getFileName(), url, request.getFileContent());
	}

	@VisibleForTesting
	String getVersionId(String version) {
		if(version == null || version.isEmpty()) {
			return null;
		}

		return version.substring(version.lastIndexOf("/") + 1);
	}

	private void responseCallback(String function, Object response) {
		if(config.getServiceResponseListener() == null) {
			return;
		}

		config.getServiceResponseListener().getResponse(function, response);
	}

	private void deletePreviousVersion(DeployRequest request) throws IOException {
		if(!request.isDeletePreviousVersions()) {
			return;
		}

		GetReleasesResponse response = getReleases();
		AtomicInteger i = new AtomicInteger(0);

		for(Release release : response.getReleases()) {
			if(i.get() > 0 && Constants.FINALIZED.equals(release.getVersion().getStatus())) {
				deleteVersion(release.getVersion().getName());
			}

			i.incrementAndGet();
		}
	}

	private String getVersionName(String version) {
		if(version == null || version.isEmpty()) {
			throw new IllegalArgumentException("Version field is mandatory!");
		}

		String versionName = SITES + config.getSiteId() + VERSIONS + version;

		Pattern p = Pattern.compile(SITES + config.getSiteId() + VERSIONS + ".*");
		Matcher m = p.matcher(version);

		if (m.matches()) {
			versionName = version;
		}
		
		return versionName;
	}
	
	private void uploadFiles(String versionId, DeployRequest request, PopulateFilesResponse populateFilesResponse) throws IOException, NoSuchAlgorithmException {
		for(DeployItem item : request.getFiles()) {
			byte[] fileContent = FileUtils.compressAndReadFile(item.getContent());
			String checkSum = FileUtils.getSHA256Checksum(fileContent);
			
			if(populateFilesResponse.getUploadRequiredHashes() != null && !populateFilesResponse.getUploadRequiredHashes().contains(checkSum)) {
				continue;
			}
			
			UploadFileRequest uploadFilesRequest = new UploadFileRequest();
			uploadFilesRequest.setVersion(versionId);
			uploadFilesRequest.setFileContent(fileContent);
			uploadFilesRequest.setFileName(item.getName());
			uploadFile(uploadFilesRequest);
		}
	}

	private Map<String, String> generateFileListAndHash(Set<DeployItem> files) throws IOException, NoSuchAlgorithmException {
		Map<String, String> result = new HashMap<>();

		for(DeployItem file : files) {
			byte[] gzippedContent = FileUtils.compressAndReadFile(file.getContent());
			String checkSum = FileUtils.getSHA256Checksum(gzippedContent);
			result.put("/" + file.getName(), checkSum);
		}
		
		return result;
	}

	private void preValidateConfig(FirebaseHostingApiConfig firebaseRestApiConfig) {
		if (firebaseRestApiConfig == null) {
			throw new IllegalArgumentException("FirebaseRestApiConfig field is mandatory!");
		}

		if (firebaseRestApiConfig.getSiteId() == null || firebaseRestApiConfig.getSiteId().isEmpty()) {
			throw new IllegalArgumentException("Site name is mandatory!");
		}

		if (firebaseRestApiConfig.getServiceAccountFileStream() == null) {
			throw new IllegalArgumentException("Service account file stream is missing from the configuration!");
		}

		if (firebaseRestApiConfig.getSerializer() == null) {
			throw new IllegalArgumentException("Serializer is missing from the configuration!");
		}
	}
}