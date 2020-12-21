package io.github.szrnkapeter.firebase.hosting.serializer;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
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