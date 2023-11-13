package io.github.szrnkapeter.firebase.hosting.service;

import io.github.szrnkapeter.firebase.hosting.model.DeployRequest;
import io.github.szrnkapeter.firebase.hosting.model.Release;
import io.github.szrnkapeter.firebase.hosting.model.Version;

import java.io.IOException;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public interface VersionService {

    Version createVersion() throws IOException;

    void deleteVersion(String versionName) throws IOException;

    void deletePreviousVersions(DeployRequest request, List<Release> releaseList) throws IOException;

    Version finalizeVersion(String version) throws IOException;
}
