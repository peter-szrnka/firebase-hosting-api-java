package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.GetVersionFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesRequest;
import io.github.szrnkapeter.firebase.hosting.model.PopulateFilesResponse;
import io.github.szrnkapeter.firebase.hosting.model.UploadFileRequest;
import io.github.szrnkapeter.firebase.hosting.service.AbstractUtilityService;
import io.github.szrnkapeter.firebase.hosting.service.FileService;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.Constants;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.FILES;
import static io.github.szrnkapeter.firebase.hosting.util.Constants.SITES;
import static io.github.szrnkapeter.firebase.hosting.util.Constants.VERSIONS;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public class FileServiceImpl extends AbstractUtilityService implements FileService {

    public FileServiceImpl(FirebaseHostingApiConfig config, String accessToken) {
        super(config, accessToken);
    }

    @Override
    public GetVersionFilesResponse getVersionFiles(String versionName) throws IOException {
        GetVersionFilesResponse response = ConnectionUtils.openHTTPGetConnection(config, GetVersionFilesResponse.class, accessToken, versionName + FILES);

        responseCallback("getVersionFiles", response);
        return response;
    }

    @Override
    public PopulateFilesResponse populateFiles(PopulateFilesRequest request, String version) throws IOException {
        String data = config.getSerializer().toJson(PopulateFilesRequest.class, request);
        PopulateFilesResponse response = ConnectionUtils.openSimpleHTTPPostConnection(config, PopulateFilesResponse.class, accessToken, SITES + config.getSiteId() + VERSIONS + version + ":populateFiles", data, "populateFiles");

        responseCallback("populateFiles", response);
        return response;
    }

    @Override
    public void uploadFile(UploadFileRequest request) throws NoSuchAlgorithmException, IOException {
        String calculatedHash = FileUtils.getSHA256Checksum(request.getFileContent());
        String url = Constants.UPLOAD_FIREBASE_API_URL + "upload/" + SITES + config.getSiteId() + VERSIONS + request.getVersion() + FILES + "/" + calculatedHash;

        if(request.getUploadUrl() != null) {
            url = request.getUploadUrl() + "/" + calculatedHash;
        }

        ConnectionUtils.uploadFile(config, accessToken, request.getFileName(), url, request.getFileContent());
    }

    @Override
    public void uploadFiles(String versionId, Set<DeployItem> files, List<String> requiredHashes) throws IOException, NoSuchAlgorithmException {
        for(DeployItem item : files) {
            byte[] fileContent = FileUtils.compressAndReadFile(item.getContent());
            String checkSum = FileUtils.getSHA256Checksum(fileContent);

            if(requiredHashes != null && !requiredHashes.contains(checkSum)) {
                continue;
            }

            UploadFileRequest uploadFilesRequest = new UploadFileRequest();
            uploadFilesRequest.setVersion(versionId);
            uploadFilesRequest.setFileContent(fileContent);
            uploadFilesRequest.setFileName(item.getName());
            uploadFile(uploadFilesRequest);
        }
    }
}
