package io.github.szrnkapeter.firebase.hosting.service;

import io.github.szrnkapeter.firebase.hosting.model.GetReleasesResponse;
import io.github.szrnkapeter.firebase.hosting.model.Release;

import java.io.IOException;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public interface ReleaseService {

    Release createRelease(String version) throws IOException;

    GetReleasesResponse getReleases() throws IOException;
}
