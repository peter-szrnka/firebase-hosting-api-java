package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class GetReleasesResponse implements Serializable {

	private static final long serialVersionUID = -3469350443495966135L;
	private List<Release> releases;
	private String nextPageToken;

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetReleasesResponse [releases=" + releases + ", nextPageToken=" + nextPageToken + "]";
	}
}