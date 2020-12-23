package io.github.szrnkapeter.firebase.hosting.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	 * @param fis Input stream
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
	 * @param fileStream The input stream that we want to compress.
	 * 
	 * @return A new {@link GZIPInputStream} instance.
	 * @throws Exception Any unwanted exception
	 * 
	 * @since 0.2
	 */
	public static byte[] compressAndReadFile(byte[] fileContent) throws Exception {
		ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContent);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzipOS = new GZIPOutputStream(bos);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = fileStream.read(buffer)) != -1) {
			gzipOS.write(buffer, 0, len);
		}

		// Read resources
		byte[] output = bos.toByteArray();

		// close resources
		gzipOS.close();
		bos.close();
		fileStream.close();

		return output;
	}
}