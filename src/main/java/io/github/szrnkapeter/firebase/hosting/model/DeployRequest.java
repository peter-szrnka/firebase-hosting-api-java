package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 0.4
 */
public class DeployRequest implements Serializable {

	private static final long serialVersionUID = -4937390204485837951L;

	private boolean cleanDeploy = true;
	private boolean deletePreviousVersions = false;
	private Set<DeployItem> files;

	public boolean isCleanDeploy() {
		return cleanDeploy;
	}

	public void setCleanDeploy(boolean cleanDeploy) {
		this.cleanDeploy = cleanDeploy;
	}

	public Set<DeployItem> getFiles() {
		return files;
	}

	public void setFiles(Set<DeployItem> files) {
		this.files = files;
	}

	public boolean isDeletePreviousVersions() {
		return deletePreviousVersions;
	}

	public void setDeletePreviousVersions(boolean deletePreviousVersions) {
		this.deletePreviousVersions = deletePreviousVersions;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeployRequest [cleanDeploy=" + cleanDeploy + ", deletePreviousVersions=" + deletePreviousVersions
				+ ", files=" + files + "]";
	}
}