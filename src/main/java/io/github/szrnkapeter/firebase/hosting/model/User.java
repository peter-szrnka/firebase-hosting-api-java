package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public class User implements Serializable {

	private static final long serialVersionUID = -7820470328407025368L;
	private String email;
	private String imageUrl;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [email=" + email + ", imageUrl=" + imageUrl + "]";
	}
}