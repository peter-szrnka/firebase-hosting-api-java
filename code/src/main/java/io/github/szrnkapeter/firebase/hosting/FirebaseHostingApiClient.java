package io.github.szrnkapeter.firebase.hosting;

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
import io.github.szrnkapeter.firebase.hosting.service.FileService;
import io.github.szrnkapeter.firebase.hosting.service.ReleaseService;
import io.github.szrnkapeter.firebase.hosting.service.VersionService;
import io.github.szrnkapeter.firebase.hosting.service.impl.FileServiceImpl;
import io.github.szrnkapeter.firebase.hosting.service.impl.ReleaseServiceImpl;
import io.github.szrnkapeter.firebase.hosting.service.impl.VersionServiceImpl;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;
import io.github.szrnkapeter.firebase.hosting.util.GoogleCredentialUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.szrnkapeter.firebase.hosting.util.ConfigValidator.preValidateConfig;
import static io.github.szrnkapeter.firebase.hosting.util.FileUtils.generateFileListAndHash;
import static io.github.szrnkapeter.firebase.hosting.util.VersionUtils.getVersionId;
import static io.github.szrnkapeter.firebase.hosting.util.VersionUtils.getVersionName;

/**
 * Firebase REST API client for Java
 *
 * @author Peter Szrnka
 * @since 0.2
 */
public class FirebaseHostingApiClient {

    private final FirebaseHostingApiConfig config;
    private final ReleaseService releaseService;
    private final VersionService versionService;
    private final FileService fileService;

    public static FirebaseHostingApiClient newClient(FirebaseHostingApiConfig config) {
        String accessToken = GoogleCredentialUtils.getAccessToken(config);

        ReleaseService releaseService = new ReleaseServiceImpl(config, accessToken);
        VersionService versionService = new VersionServiceImpl(config, accessToken);
        FileService fileService = new FileServiceImpl(config, accessToken);

        return new FirebaseHostingApiClient(config, releaseService, versionService, fileService);
    }

    /**
     * Default constructor.
     *
     * @param firebaseRestApiConfig A new {@link FirebaseHostingApiConfig}
     * @since 0.2
     */
    protected FirebaseHostingApiClient(FirebaseHostingApiConfig firebaseRestApiConfig, ReleaseService releaseService,
                                       VersionService versionService, FileService fileService) {
        preValidateConfig(firebaseRestApiConfig);

        config = firebaseRestApiConfig;
        this.releaseService = releaseService;
        this.versionService = versionService;
        this.fileService = fileService;
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
     * <p>
     * If it's a clean deploy, all previous files will be deleted.
     *
     * @param request A {@link DeployRequest} instance.
     * @return A new {@link DeployResponse} object.
     * @throws InterruptedException     Any exception occurred in this service.
     * @throws IOException              Any exception occurred in this service.
     * @throws NoSuchAlgorithmException Any exception occurred in this service.
     * @since 0.4
     */
    public DeployResponse createDeploy(DeployRequest request) throws InterruptedException, IOException, NoSuchAlgorithmException {
        if (!request.isCleanDeploy()) {
            GetReleasesResponse getReleases = getReleases();

            if (getReleases == null || getReleases.getReleases() == null || getReleases.getReleases().isEmpty()) {
                return null;
            }

            String versionId = getVersionId(getReleases.getReleases().get(0).getVersion().getName());
            GetVersionFilesResponse getVersionFiles = getVersionFiles(versionId);

            Set<String> newFileNames = request.getFiles().stream().map(DeployItem::getName).collect(Collectors.toSet());

            for (FileDetails file : getVersionFiles.getFiles()) {
                String fileName = file.getPath().substring(1);
                if (fileName.startsWith("__/") || newFileNames.contains(fileName)) {
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
        fileService.uploadFiles(versionId, request.getFiles(), populateFilesResponse.getUploadRequiredHashes());

        // Finalize the new version
        finalizeVersion(versionId);

        // Create the release
        Release newRelease = createRelease(versionId);

        // Post delete earlier deployments
        versionService.deletePreviousVersions(request, getReleases().getReleases());

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
     * @since 0.2
     */
    public Release createRelease(String version) throws IOException {
        return releaseService.createRelease(getVersionName(config, version));
    }

    /**
     * Creates a new version by the given parameters. Called endpoint:
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/create">sites.versions/create</a>
     *
     * @return A new {@link Version}
     * @throws IOException Any unwanted Exception
     * @since 0.2
     */
    public Version createVersion() throws IOException {
        return versionService.createVersion();
    }

    /**
     * Deletes a version by ID. Called endpoint:
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/delete">sites.versions/delete</a>
     *
     * @param version The version ID that we want to delete.
     * @throws IOException Any unwanted Exception
     * @since 0.2
     */
    public void deleteVersion(String version) throws IOException {
        versionService.deleteVersion(getVersionName(config, version));
    }

    /**
     * Updates the status of the given version to "finalized". Called endpoint:
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/patch">sites.versions/patch</a>
     *
     * @param version The version ID that we want to finalize.
     * @return A new {@link Version}
     * @throws IOException Any unwanted Exception
     * @since 0.2
     */
    public Version finalizeVersion(String version) throws IOException {
        return versionService.finalizeVersion(version);
    }

    /**
     * Returns with the list of releases. It calls the list releases endpoint:
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.releases/list">sites.releases/list</a>
     *
     * @return A new {@link GetReleasesResponse} instance
     * @throws MalformedURLException The unexpected MalformedURLException
     * @throws IOException           The unexpected IOException
     * @since 0.2
     */
    public GetReleasesResponse getReleases() throws IOException {
        return releaseService.getReleases();
    }

    /**
     * Returns with all files of a given version. Called endpoint:
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions.files/list">sites.versions.files/list</a>
     *
     * @param version Firebase version name
     * @return A {@link GetVersionFilesResponse} response
     * @throws IOException The unexpected exception
     * @since 0.2
     */
    public GetVersionFilesResponse getVersionFiles(String version) throws IOException {
        return fileService.getVersionFiles(getVersionName(config, version));
    }

    /**
     * Calls the populateFiles endpoint.
     * <a href="https://firebase.google.com/docs/reference/hosting/rest/v1beta1/sites.versions/populateFiles">sites.versions/populateFiles</a>
     *
     * @param request A {@link PopulateFilesRequest} request object
     * @param version Firebase version name
     * @return A {@link PopulateFilesResponse} response.
     * @throws IOException The unexpected exception
     * @since 0.2
     */
    public PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws IOException {
        return fileService.populateFiles(request, version);
    }

    /**
     * Uploads a file.
     *
     * @param request A {@link UploadFileRequest} instance.
     * @throws NoSuchAlgorithmException Thrown by getSHA256Checksum
     * @throws IOException              The unexpected exception
     * @since 0.2
     */
    public void uploadFile(UploadFileRequest request) throws NoSuchAlgorithmException, IOException {
        fileService.uploadFile(request);
    }
}