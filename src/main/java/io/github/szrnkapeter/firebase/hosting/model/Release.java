package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class Release implements Serializable {

	private static final long serialVersionUID = -6883408224672543765L;

	private String name;
	private String type;
	private Date releaseTime;
	private User releaseUser;
	private Version version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public User getReleaseUser() {
		return releaseUser;
	}

	public void setReleaseUser(User releaseUser) {
		this.releaseUser = releaseUser;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Release [name=" + name + ", type=" + type + ", releaseTime=" + releaseTime + ", releaseUser="
				+ releaseUser + ", version=" + version + "]\r\n";
	}
}