package io.github.szrnkapeter.firebase.hosting.util;

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
	public void test02_() throws Exception {
		byte[] response = FileUtils.compressAndReadFile("test".getBytes());
		Assert.assertEquals(24, response.length);
	}
}