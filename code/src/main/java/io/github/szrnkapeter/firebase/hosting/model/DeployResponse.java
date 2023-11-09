package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 0.4
 */
public class DeployResponse implements Serializable {

	private static final long serialVersionUID = 2766782678797853526L;

	private Release release;
	
	public DeployResponse() {}
	
	public DeployResponse(Release r) {
		release = r;
	}

	public Release getRelease() {
		return release;
	}

	public void setRelease(Release release) {
		this.release = release;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeployResponse [release=" + release + "]";
	}
}