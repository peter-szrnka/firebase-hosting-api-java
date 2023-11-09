package io.github.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.Arrays;

public class DeployItem implements Serializable {

	private static final long serialVersionUID = 6243049057242711371L;

	private String name;
	private byte[] content;
	
	public DeployItem() {}
	
	public DeployItem(String n, byte[] c) {
		name = n;
		content = c;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(content);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeployItem other = (DeployItem) obj;
		if (!Arrays.equals(content, other.content))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeployItem [name=" + name + ", content=" + Arrays.toString(content) + "]";
	}
}