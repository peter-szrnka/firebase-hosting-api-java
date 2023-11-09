package io.github.szrnkapeter.firebase.hosting.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Test;

class FileUtilsTest {
	
	@Test
	void testPrivateConstructor() {
		assertDoesNotThrow(() -> TestUtils.testPrivateConstructor(FileUtils.class));
	}

	@Test
	void test01_GetSHA256Checksum() throws Exception {
		String response = FileUtils.getSHA256Checksum("test".getBytes());
		assertNotNull(response);
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", response);
	}

	@Test
	void test02_CompressAndReadFile() throws Exception {
		byte[] response = FileUtils.compressAndReadFile("test".getBytes());
		assertEquals(24, response.length);
		assertEquals((byte) (GZIPInputStream.GZIP_MAGIC >> 8), response[1]);
	}
	
	@Test
	void test03_GetRemoteFile_Fail() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test0.txt").toURI().toURL().toString());
		assertEquals(0, response.length);
	}
	
	@Test
	void test04_GetRemoteFile_Success() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test1.txt").toURI().toURL().toString());
		assertEquals(7, response.length);
	}
}