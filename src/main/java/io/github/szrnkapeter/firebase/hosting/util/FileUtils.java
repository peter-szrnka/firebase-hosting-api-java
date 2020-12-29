package io.github.szrnkapeter.firebase.hosting.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class to provide file related methods.
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FileUtils {

	private FileUtils() {
	}

	/**
	 * Returns with the SHA-256 checksum of the given input stream.
	 * 
	 * @param fileContent File content in byte array.
	 * @return The SHA-256 hash
	 * @throws Exception Any exception thrown by the method.
	 * 
	 * @since 0.2
	 */
	public static String getSHA256Checksum(byte[] fileContent) throws Exception {
		MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileContent);

		while ((bytesCount = bis.read(byteArray)) != -1) {
			shaDigest.update(byteArray, 0, bytesCount);
		}

		bis.close();

		byte[] bytes = shaDigest.digest();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	/**
	 * Compresses and returns with a gzipped input stream.
	 * 
	 * @param fileContent File content in byte array.
	 * 
	 * @return A new {@link GZIPInputStream} instance.
	 * @throws Exception Any unwanted exception
	 * 
	 * @since 0.2
	 */
	public static byte[] compressAndReadFile(byte[] fileContent) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream(bos);
		out.write(fileContent);
		out.close();
		return bos.toByteArray();
	}

	/**
	 * Downloads a remote file as a byte array.
	 * 
	 * @param urlString The input URL string
	 * @return A new byte array
	 * @throws Exception Any exception caused by the method process
	 * 
	 * @since0.4
	 */
	public static byte[] getRemoteFile(String urlString) throws Exception {
		URL url = new URL(urlString);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = url.openStream();
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);
			}
		} catch (IOException e) {
			System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
			e.printStackTrace();
			// Perform any other exception handling that's appropriate.
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return baos.toByteArray();
	}
}