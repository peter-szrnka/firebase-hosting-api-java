package io.github.szrnkapeter.firebase.hosting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
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
	 * Creates a new release.
	 * 
	 * @param version The version ID that we want to release
	 * @return A new {@link Release}
	 * @throws Exception An unwanted Exception
	 * 
	 * @since 0.2
	 */
	public Release createRelease(String version) throws Exception {	
		return ConnectionUtils.openSimpleHTTPPostConnection(config, Release.class, accessToken,
			SITES + config.getSiteName() + "/releases?versionName=" + getVersionName(version), null);
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
		return ConnectionUtils.openSimpleHTTPPostConnection(config, Version.class, accessToken,
				SITES + config.getSiteName() + "/versions", "{}");
	}

	/**
	 * Deletes a version by ID. 
	 * 
	 * @param version The version ID that we want to delete.
	 * @throws Exception
	 * 
	 * @since 0.2
	 */
	public void deleteVersion(String version) throws Exception {
		ConnectionUtils.openSimpleHTTPConnection("DELETE", config, null, accessToken, SITES + config.getSiteName() + VERSIONS + version, null);
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
		return ConnectionUtils.openSimpleHTTPConnection("PATCH", config, Version.class, accessToken,
				SITES + config.getSiteName() + VERSIONS + version + "?update_mask=status", "{ \"status\": \"FINALIZED\" }");
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
	 * @throws Exception The unexpected exception
	 * 
	 * @since 0.2
	 */
	public PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws Exception {
		String data = SerializerFactory.getSerializer(config.getSerializer()).toJson(PopulateFilesRequest.class, request);
		return ConnectionUtils.openSimpleHTTPPostConnection(config, PopulateFilesResponse.class, accessToken, SITES + config.getSiteName() + VERSIONS + version + ":populateFiles", data);
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