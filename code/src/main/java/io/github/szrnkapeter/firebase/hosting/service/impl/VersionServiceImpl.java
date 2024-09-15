package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.Version;
import io.github.szrnkapeter.firebase.hosting.service.AbstractUtilityService;
import io.github.szrnkapeter.firebase.hosting.service.VersionService;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.*;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class VersionServiceImpl extends AbstractUtilityService implements VersionService {

    private static final String CREATE_VERSION = "createVersion";

    public VersionServiceImpl(FirebaseHostingApiConfig config, String accessToken) {
        super(config, accessToken);
    }

    @Override
    public Version createVersion() throws IOException {
        Version newVersion = ConnectionUtils.openSimpleHTTPPostConnection(config, Version.class, accessToken,
                SITES + config.getSiteId() + "/versions", "{}", CREATE_VERSION);

        responseCallback(CREATE_VERSION, newVersion);
        return newVersion;
    }

    @Override
    public void deleteVersion(String versionName) throws IOException {
        ConnectionUtils.openSimpleHTTPConnection("DELETE", config, null, accessToken, versionName, null, "deleteVersion");
    }

    @Override
    public void deletePreviousVersions(DeployRequest request, List<Release> releaseList) throws InterruptedException, IOException {
        if (!request.isDeletePreviousVersions()) {
            return;
        }

        // Prefilter releases
        List<String> filteredReleases = new ArrayList<>();

        for (int i = 1; i < releaseList.size(); i++) {
            Release release = releaseList.get(i);
            if (Constants.DELETED.equals(release.getVersion().getStatus()) || !Constants.FINALIZED.equals(release.getVersion().getStatus())) {
                continue;
            }

            filteredReleases.add(release.getVersion().getName());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        CountDownLatch latch = new CountDownLatch(config.isDisableAsync() ? 0 : filteredReleases.size() - 1);

        for (String releaseName : filteredReleases) {
            if (config.isDisableAsync()) {
                deleteVersion(releaseName);
            } else {
                executorService.execute(() -> deleteVersionAsync(releaseName, latch));
            }
        }

        latch.await();
        executorService.shutdown();
    }

    void deleteVersionAsync(String versionName, CountDownLatch latch) {
        try {
            deleteVersion(versionName);
        } catch (IOException e) {
            responseCallback(UPLOAD_FILES, e);
        } finally {
            latch.countDown();
        }
    }

    @Override
    public Version finalizeVersion(String version) throws IOException {
        Version newVersion = ConnectionUtils.openSimpleHTTPConnection("PATCH", config, Version.class, accessToken,
                SITES + config.getSiteId() + VERSIONS + version + "?update_mask=status", "{ \"status\": \"FINALIZED\" }", "finalizeVersion");

        responseCallback("finalizeVersion", newVersion);
        return newVersion;
    }
}
