package io.github.szrnkapeter.firebase.hosting;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import io.github.szrnkapeter.firebase.hosting.model.FileDetails;

public class FirebaseHostingApiClientTestHelper {
	
	private FirebaseHostingApiClientTestHelper() {
	}
	
	public static FileDetails createFileDetails(String filePath, String status) {
		FileDetails fd = new FileDetails();
		fd.setHash(UUID.randomUUID().toString());
		fd.setPath(filePath);
		fd.setStatus(status);
		
		return fd;
	}
	
	public static DeployItem createDeployItem(String fileName, String mockContentPath) throws Exception {
		DeployItem di = new DeployItem();
		di.setName(fileName);
		di.setContent(Files.readAllBytes(new File(mockContentPath).toPath()));
		
		return di;
	}
}