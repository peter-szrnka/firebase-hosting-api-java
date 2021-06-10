package io.github.szrnkapeter.firebase.hosting.util;

import java.io.File;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void test01_GetSHA256Checksum() throws Exception {
		String response = FileUtils.getSHA256Checksum("test".getBytes());
		Assert.assertNotNull(response);
		Assert.assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", response);
	}

	@Test
	public void test02_CompressAndReadFile() throws Exception {
		byte[] response = FileUtils.compressAndReadFile("test".getBytes());
		Assert.assertEquals(24, response.length);
		Assert.assertEquals((byte) (GZIPInputStream.GZIP_MAGIC >> 8), response[1]);
	}
	
	@Test
	public void test03_GetRemoteFile_Fail() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test0.txt").toURI().toURL().toString());
		Assert.assertEquals(0, response.length);
	}
	
	@Test
	public void test04_GetRemoteFile_Success() throws Exception {
		byte[] response = FileUtils.getRemoteFile( new File("src/test/resources/test1.txt").toURI().toURL().toString());
		Assert.assertEquals(7, response.length);
	}
}