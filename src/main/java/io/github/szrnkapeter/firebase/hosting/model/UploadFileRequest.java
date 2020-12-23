package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 0.2
 */
public class UploadFileRequest implements Serializable {

	private static final long serialVersionUID = -1537341206199522293L;

	private String version;
	private byte[] fileContent;
	private String uploadUrl;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UploadFileRequest [version=" + version + ", uploadUrl=" + uploadUrl + "]";
	}
}