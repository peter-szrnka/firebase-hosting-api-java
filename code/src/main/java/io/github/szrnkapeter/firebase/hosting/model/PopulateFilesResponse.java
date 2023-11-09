package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 0.2
 */
public class PopulateFilesResponse implements Serializable {

	private static final long serialVersionUID = 7321845972838371319L;

	private List<String> uploadRequiredHashes;
	private String uploadUrl;

	public List<String> getUploadRequiredHashes() {
		return uploadRequiredHashes;
	}

	public void setUploadRequiredHashes(List<String> uploadRequiredHashes) {
		this.uploadRequiredHashes = uploadRequiredHashes;
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
		return "PopulateFilesResponse [uploadRequiredHashes=" + uploadRequiredHashes + ", uploadUrl=" + uploadUrl + "]";
	}
}