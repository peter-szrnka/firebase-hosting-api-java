package hu.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class GetVersionFilesResponse implements Serializable {

	private static final long serialVersionUID = -2475440680723348233L;

	private List<FileDetails> files;

	public List<FileDetails> getFiles() {
		return files;
	}

	public void setFiles(List<FileDetails> files) {
		this.files = files;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetVersionFilesResponse [files=" + files + "]";
	}
}