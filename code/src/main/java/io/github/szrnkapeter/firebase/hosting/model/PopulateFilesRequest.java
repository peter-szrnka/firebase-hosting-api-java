package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 0.2
 */
public class PopulateFilesRequest implements Serializable {

	private static final long serialVersionUID = -3435609265185726952L;

	private Map<String, String> files;

	public Map<String, String> getFiles() {
		return files;
	}

	public void setFiles(Map<String, String> files) {
		this.files = files;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PopulateFilesRequest [files=" + files + "]";
	}
}