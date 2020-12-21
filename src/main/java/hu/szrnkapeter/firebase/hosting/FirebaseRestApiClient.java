package hu.szrnkapeter.firebase.hosting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.szrnkapeter.firebase.hosting.config.FirebaseRestApiConfig;
import hu.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import hu.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import hu.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import hu.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

/**
 * Firebase REST API client for Java
 * 
 * @author Peter Szrnka
 * @since 0.1
 */
public class FirebaseRestApiClient {

	private static final String SITES = "sites/";

	private FirebaseRestApiConfig config;
	private String accessToken;

	public FirebaseRestApiClient() {
	}

	public FirebaseRestApiClient(FirebaseRestApiConfig firebaseRestApiConfig) throws IOException {
		config = firebaseRestApiConfig;
		accessToken = GoogleCredentialUtils.getAccessToken(config);
	}

	/**
	 * Returns with the list of releases
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public GetReleasesResponse getReleases() throws Exception {
		return ConnectionUtils.openHTTPGetConnection(config, GetReleasesResponse.class, accessToken,
				SITES + config.getSiteName() + "/releases");
	}

	/**
	 * Returns with all files of a given version
	 * 
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public GetVersionFilesResponse getVersionFiles(String version) throws Exception {
		String url = SITES + config.getSiteName() + "/versions/" + version + "/files";

		Pattern p = Pattern.compile(SITES + config.getSiteName() + "/versions/.*");
		Matcher m = p.matcher(version);

		if (m.matches()) {
			url = version + "/files";
		}

		return ConnectionUtils.openHTTPGetConnection(config, GetVersionFilesResponse.class, accessToken, url);
	}
}