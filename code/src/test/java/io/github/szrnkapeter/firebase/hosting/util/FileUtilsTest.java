package io.github.szrnkapeter.firebase.hosting.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import io.github.szrnkapeter.firebase.hosting.model.DeployItem;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author Peter Szrnka
 * @since 0.2
 */
class FileUtilsTest {
	
	@Test
	void testPrivateConstructor() {
		assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(FileUtils.class));
	}

	@Test
	@SneakyThrows
	void shouldGenerateFileListAndHash() {
		// arrange
		Set<DeployItem> fileList = new HashSet<>();
		fileList.add(new DeployItem("file1.txt", "test".getBytes()));

		// act
		Map<String, String> response = FileUtils.generateFileListAndHash(fileList);

		// assert
		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals("f7beb20179aee76b26b8b4b0840a89a70b1fbb72333892df6c54fe1010640cb3", response.get("/file1.txt"));
	}

	@Test
	void shouldGetSHA256Checksum() throws Exception {
		String response = FileUtils.getSHA256Checksum("test".getBytes());
		assertNotNull(response);
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", response);
	}

	@Test
	void shouldCompressAndReadFile() throws Exception {
		byte[] response = FileUtils.compressAndReadFile("test".getBytes());
		assertEquals(24, response.length);
		assertEquals((byte) (GZIPInputStream.GZIP_MAGIC >> 8), response[1]);
	}
	
	@Test
	void shouldGetRemoteFileFail() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test0.txt").toURI().toURL().toString());
		assertEquals(0, response.length);
	}
	
	@Test
	void shouldGetRemoteFileSucceed() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test1.txt").toURI().toURL().toString());
		assertEquals(7, response.length);
	}
}