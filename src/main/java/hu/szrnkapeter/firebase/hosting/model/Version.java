package hu.szrnkapeter.firebase.hosting.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Version implements Serializable {

	private static final long serialVersionUID = -1388551022158308920L;
	private String name;
	private String status;
	private Map<String, String> config;
	private Map<String, String> labels;
	//@JsonSerialize(using = LocalDateTimeSerializer.class)
	private Date createTime;
	private User createUser;
	//@JsonSerialize(using = LocalDateTimeSerializer.class)
	private Date finalizeTime;
	private User finalizeUser;
	private String fileCount;
	private String versionBytes;
	private User deleteUser;
	private Date deleteTime;
	
	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getFinalizeTime() {
		return finalizeTime;
	}

	public void setFinalizeTime(Date finalizeTime) {
		this.finalizeTime = finalizeTime;
	}

	public User getFinalizeUser() {
		return finalizeUser;
	}

	public void setFinalizeUser(User finalizeUser) {
		this.finalizeUser = finalizeUser;
	}

	public String getFileCount() {
		return fileCount;
	}

	public void setFileCount(String fileCount) {
		this.fileCount = fileCount;
	}

	public String getVersionBytes() {
		return versionBytes;
	}

	public void setVersionBytes(String versionBytes) {
		this.versionBytes = versionBytes;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	public User getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(User deleteUser) {
		this.deleteUser = deleteUser;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Version [name=" + name + ", status=" + status + ", config=" + config + ", labels=" + labels
				+ ", createTime=" + createTime + ", createUser=" + createUser + ", finalizeTime=" + finalizeTime
				+ ", finalizeUser=" + finalizeUser + ", fileCount=" + fileCount + ", versionBytes=" + versionBytes
				+ ", deleteUser=" + deleteUser + ", deleteTime=" + deleteTime + "]";
	}
}