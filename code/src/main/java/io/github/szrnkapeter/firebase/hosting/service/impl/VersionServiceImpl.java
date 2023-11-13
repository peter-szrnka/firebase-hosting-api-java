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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.SITES;
import static io.github.szrnkapeter.firebase.hosting.util.Constants.VERSIONS;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class VersionServiceImpl extends AbstractUtilityService implements VersionService {

    public VersionServiceImpl(FirebaseHostingApiConfig config, String accessToken) {
        super(config, accessToken);
    }

    @Override
    public Version createVersion() throws IOException {
        Version newVersion = ConnectionUtils.openSimpleHTTPPostConnection(config, Version.class, accessToken,
                SITES + config.getSiteId() + "/versions", "{}", "createVersion");

        responseCallback("createVersion", newVersion);
        return newVersion;
    }

    @Override
    public void deleteVersion(String versionName) throws IOException {
        ConnectionUtils.openSimpleHTTPConnection("DELETE", config, null, accessToken, versionName, null, "deleteVersion");
    }

    @Override
    public void deletePreviousVersions(DeployRequest request, List<Release> releaseList) throws IOException {
        if(!request.isDeletePreviousVersions()) {
            return;
        }

        AtomicInteger i = new AtomicInteger(0);

        for(Release release : releaseList) {
            if(i.get() > 0 && Constants.FINALIZED.equals(release.getVersion().getStatus())) {
                deleteVersion(release.getVersion().getName());
            }

            i.incrementAndGet();
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
