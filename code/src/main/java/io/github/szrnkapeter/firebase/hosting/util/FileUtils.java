package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.model.DeployItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class to provide file related methods.
 * 
 * @author Peter Szrnka
 * @since 0.2
 */
public class FileUtils {
	
	private static final Logger logger = Logger.getLogger(FileUtils.class.getName());  

	private FileUtils() {
	}

	public static Map<String, String> generateFileListAndHash(Set<DeployItem> files) throws IOException, NoSuchAlgorithmException {
		Map<String, String> result = new HashMap<>();

		for(DeployItem file : files) {
			byte[] gzippedContent = compressAndReadFile(file.getContent());
			String checkSum = getSHA256Checksum(gzippedContent);
			result.put("/" + file.getName(), checkSum);
		}

		return result;
	}

	/**
	 * Returns with the SHA-256 checksum of the given input stream.
	 * 
	 * @param fileContent File content in byte array.
	 * @return The SHA-256 hash
	 * @throws NoSuchAlgorithmException Any exception thrown by the method.
	 * @throws IOException  Any exception thrown by the method.
	 * @since 0.2
	 */
	public static String getSHA256Checksum(byte[] fileContent) throws NoSuchAlgorithmException, IOException {
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
	 * @throws IOException Any unwanted exception
	 * 
	 * @since 0.2
	 */
	public static byte[] compressAndReadFile(byte[] fileContent) throws IOException {
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
	 * @throws IOException Any exception caused by the method process
	 * 
	 * @since 0.4
	 */
	public static byte[] getRemoteFile(String urlString) throws IOException {
		URL url = new URL(urlString);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream is = url.openStream()) {
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);
			}
		} catch (IOException e) {
			logger.warning(String.format("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage()));
			// Perform any other exception handling that's appropriate.
		}

		return baos.toByteArray();
	}
}