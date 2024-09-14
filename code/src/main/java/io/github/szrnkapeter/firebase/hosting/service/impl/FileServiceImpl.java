package io.github.szrnkapeter.firebase.hosting.service.impl;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;
import io.github.szrnkapeter.firebase.hosting.model.*;
import io.github.szrnkapeter.firebase.hosting.service.AbstractUtilityService;
import io.github.szrnkapeter.firebase.hosting.service.FileService;
import io.github.szrnkapeter.firebase.hosting.util.ConnectionUtils;
import io.github.szrnkapeter.firebase.hosting.util.Constants;
import io.github.szrnkapeter.firebase.hosting.util.FileUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.*;

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

        if (request.getUploadUrl() != null) {
            url = request.getUploadUrl() + "/" + calculatedHash;
        }

        ConnectionUtils.uploadFile(config, accessToken, request.getFileName(), url, request.getFileContent());
    }

    @Override
    public void uploadFiles(String versionId, Set<DeployItem> files, List<String> requiredHashes)
            throws IOException, NoSuchAlgorithmException, InterruptedException {

        List<FileUploadItem> fileUploadItems = new ArrayList<>();

        for (DeployItem item : files) {
            byte[] fileContent = FileUtils.compressAndReadFile(item.getContent());
            String checkSum = FileUtils.getSHA256Checksum(fileContent);

            if (requiredHashes != null && !requiredHashes.contains(checkSum)) {
                continue;
            }

            if (!config.isDisableAsync()) {
                // In case of async mode, we will upload the files in parallel
                fileUploadItems.add(new FileUploadItem(fileContent, checkSum));
            } else {
                uploadFileItem(fileContent, versionId, checkSum);
            }
        }

        if (config.isDisableAsync()) {
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        CountDownLatch latch = new CountDownLatch(fileUploadItems.size());

        fileUploadItems.forEach(item -> executorService.execute(() -> uploadFileAsync(item, versionId, latch)));

        latch.await();
        executorService.shutdown();
    }

    void uploadFileAsync(FileUploadItem item, String versionId, CountDownLatch latch) {
        try {
            uploadFileItem(item.getFileContent(), versionId, item.getCheckSum());
        } catch (NoSuchAlgorithmException | IOException e) {
            responseCallback("uploadFiles", e);
        } finally {
            latch.countDown();
        }
    }

    private void uploadFileItem(byte[] fileContent, String versionId, String checkSum) throws NoSuchAlgorithmException, IOException {
        UploadFileRequest request = new UploadFileRequest();
        request.setFileContent(fileContent);
        request.setVersion(versionId);
        request.setFileName(checkSum);
        uploadFile(request);
    }
}
