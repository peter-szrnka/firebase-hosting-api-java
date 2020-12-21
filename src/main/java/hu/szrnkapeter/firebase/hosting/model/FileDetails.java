package hu.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class FileDetails implements Serializable {

	private static final long serialVersionUID = -2427694455038032293L;

	private String path;
	private String hash;
	private String status;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileDetails [path=" + path + ", hash=" + hash + ", status=" + status + "]";
	}
}