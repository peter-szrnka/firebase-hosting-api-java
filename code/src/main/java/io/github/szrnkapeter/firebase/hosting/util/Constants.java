package io.github.szrnkapeter.firebase.hosting.util;

/** 
 * @author Peter Szrnka
 * @since 0.2
 */
public class Constants {
	
	private Constants() {
	}

	public static final int CHECKSUM_BUFFER_SIZE = 1024;
	public static final int STREAM_BUFFER_SIZE = 4096;

	public static final String FILES = "/files";
	public static final String VERSIONS = "/versions/";
	public static final String SITES = "sites/";

	public static final String DELETED = "DELETED";
	public static final String FINALIZED = "FINALIZED";
	public static final String POST = "POST";
	public static final String PATCH = "PATCH";
	public static final String FIREBASE_API_URL = "https://firebasehosting.googleapis.com/v1beta1/";
	public static final String CHARSET = "UTF-8";
	public static final String UPLOAD_FIREBASE_API_URL = "https://upload-firebasehosting.googleapis.com/";
	public static final String UPLOAD_FILES = "uploadFiles";
}