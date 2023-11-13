package io.github.szrnkapeter.firebase.hosting.service;

import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesRequest;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.UploadFileRequest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public interface FileService {

    GetVersionFilesResponse getVersionFiles(String versionName) throws IOException;

    PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws IOException;

    void uploadFile(UploadFileRequest request) throws NoSuchAlgorithmException, IOException;

    void uploadFiles(String versionId, Set<DeployItem> files, List<String> requiredHashes) throws IOException, NoSuchAlgorithmException;
}
