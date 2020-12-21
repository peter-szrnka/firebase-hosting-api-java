package hu.szrnkapeter.firebase.hosting.serializer;

public interface Serializer {

	/**
	 * JSON -> Object conversion.
	 * 
	 * @param clazz
	 * @param responseBody
	 * @return
	 * @throws Exception
	 */
	<T> T getObject(Class<T> clazz, String responseBody) throws Exception;
}