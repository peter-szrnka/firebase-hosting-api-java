package io.github.szrnkapeter.firebase.hosting.serializer;

import org.junit.Assert;
import org.junit.Test;

import io.github.szrnkapeter.firebase.hosting.serializer.obj.DummyObject;

public class SerializerTest {
	
	private static final String JSON_BODY = "{\"attr1\":\"test\"}";

	private <T extends Serializer> void runTests(Class<T> clazz) throws Exception {
		T serializer = clazz.newInstance();
		DummyObject response = serializer.getObject(DummyObject.class, JSON_BODY);
		Assert.assertNotNull(response);
		Assert.assertEquals("test", response.getAttr1());
		
		String response2 = serializer.toJson(DummyObject.class, response);
		Assert.assertEquals(JSON_BODY, response2);
	}

	@Test
	public void test01_GsonSerializer() throws Exception {
		runTests(GsonSerializer.class);
	}

	@Test
	public void test02_JacksonSerializer() throws Exception {
		runTests(JacksonSerializer.class);
	}
	
	@Test
	public void test03_MoshiSerializer() throws Exception {
		runTests(MoshiSerializer.class);
	}
}