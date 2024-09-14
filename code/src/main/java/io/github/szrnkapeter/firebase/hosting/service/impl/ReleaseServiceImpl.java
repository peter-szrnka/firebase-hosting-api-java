package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.service.AbstractUtilityService;
import io.github.szrnkapeter.firebase.hosting.service.ReleaseService;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;

import java.io.IOException;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.SITES;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class ReleaseServiceImpl extends AbstractUtilityService implements ReleaseService {

    private static final String CREATE_RELEASE = "createRelease";

    public ReleaseServiceImpl(FirebaseHostingApiConfig config, String accessToken) {
        super(config, accessToken);
    }

    @Override
    public Release createRelease(String versionName) throws IOException {
        Release newRelease =  ConnectionUtils.openSimpleHTTPPostConnection(config, Release.class, accessToken,
                SITES + config.getSiteId() + "/releases?versionName=" + versionName, null, CREATE_RELEASE);

        responseCallback(CREATE_RELEASE, newRelease);
        return newRelease;
    }

    @Override
    public GetReleasesResponse getReleases() throws IOException {
        GetReleasesResponse response = ConnectionUtils.openHTTPGetConnection(config, GetReleasesResponse.class, accessToken,
                SITES + config.getSiteId() + "/releases");

        responseCallback("getReleases", response);
        return response;
    }
}
